package com.nytdacm.oa.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
@Transactional
public class UserLastActiveAspect {
    private final UserService userService;

    @Inject
    public UserLastActiveAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("execution(* com.nytdacm.oa.controller..*.*(..))")
    public void updateLastActive() {
        if (StpUtil.getLoginId() != null) {
            var id = StpUtil.getLoginIdAsLong();
            var user = userService.getUserById(id);
            user.setLastActive(Instant.now());
            userService.updateUser(user);
        }
    }
}
