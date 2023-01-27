package com.nytdacm.oa.service.impl;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.config.SecurityConfig;
import com.nytdacm.oa.exception.OaBaseException;
import com.nytdacm.oa.service.AuthService;
import com.nytdacm.oa.service.UserService;
import com.nytdacm.oa.util.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final SecurityConfig securityConfig;
    private final UserService userService;

    @Inject
    public AuthServiceImpl(SecurityConfig securityConfig, UserService userService) {
        this.securityConfig = securityConfig;
        this.userService = userService;
    }

    @Override
    public String login(String username, String password) {
        var user = userService.getUserByUsername(username);
//        if (!user.isActive()) {
//            throw new OaBaseException("用户未激活", 401);
//        }
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
