package com.nytdacm.oa.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.model.entity.User;
import com.nytdacm.oa.service.AuthService;
import com.nytdacm.oa.service.UserService;
import com.nytdacm.oa.util.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @Inject
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public User signup(@RequestBody @Valid UserSignupRequest userSignupRequest) {
        var user = new User();
        user.setUsername(userSignupRequest.username());
        user.setPassword(PasswordUtil.hashPassword(userSignupRequest.password()));
        return userService.newUser(user);
    }

    @GetMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }

    @GetMapping("/current")
    @SaCheckLogin
    public User current() {
        return userService.getUserById(StpUtil.getLoginIdAsLong());
    }
}

record UserSignupRequest(
    @NotNull(message = "用户名不能为空") String username,
    @NotNull(message = "密码不能为空") @Size(min = 6, message = "密码长度至少为6位") String password
) {
}
