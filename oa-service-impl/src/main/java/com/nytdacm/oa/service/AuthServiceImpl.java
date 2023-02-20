package com.nytdacm.oa.service;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.config.SecurityConfig;
import com.nytdacm.oa.entity.User;
import com.nytdacm.oa.exception.OaBaseException;
import com.nytdacm.oa.utils.PasswordUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final SecurityConfig securityConfig;
    private final UserService userService;

    public AuthServiceImpl(SecurityConfig securityConfig, UserService userService) {
        this.securityConfig = securityConfig;
        this.userService = userService;
    }

    @Override
    public String login(String username, String password) {
        User user;
        try {
            user = userService.getUserByUsername(username);
        } catch (OaBaseException e) {
            throw new OaBaseException("用户不存在", 401);
        }
        if (!user.getActive()) {
            throw new OaBaseException("用户未激活，请耐心等待管理员激活", 401);
        }
        if (!PasswordUtil.checkPassword(password, user.getPasswordSalt(), user.getPassword())) {
            throw new OaBaseException("密码错误", 401);
        }

        StpUtil.login(
            user.getUserId(),
            SaLoginConfig
                .setTimeout(securityConfig.getTokenExpireTime())
        );
        return StpUtil.getTokenInfo().getTokenValue();
    }
}
