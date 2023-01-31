package com.nytdacm.oa.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.model.entity.User;
import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.user.UserDto;
import com.nytdacm.oa.service.AuthService;
import com.nytdacm.oa.service.UserService;
import com.nytdacm.oa.util.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<HttpResponse<UserDto>> signup(@RequestBody @Valid UserSignupRequest userSignupRequest) {
        var user = new User();
        user.setUsername(userSignupRequest.username());
        var salt = PasswordUtil.getSalt();
        user.setPasswordSalt(salt);
        user.setPassword(PasswordUtil.hashPassword(userSignupRequest.password(), salt));
        user.setName(userSignupRequest.name());
        if (userService.count() == 0L) {
            user.setSuperAdmin(true);
            user.setActive(true);
        }
        var newUser = userService.newUser(user);
        return HttpResponse.success(200, "注册成功", UserDto.fromEntity(newUser));
    }

    @GetMapping("/login")
    public ResponseEntity<HttpResponse<String>> login(@RequestParam String username, @RequestParam String password) {
        return HttpResponse.success(200, "登录成功", authService.login(username, password));
    }

    @GetMapping("/current")
    @SaCheckLogin
    public ResponseEntity<HttpResponse<UserState>> current() {
        var user = userService.getUserById(StpUtil.getLoginIdAsLong());
        return HttpResponse.success(200, "获取成功", UserState.fromEntity(user));
    }
}

record UserSignupRequest(
    @NotNull(message = "用户名不能为空") @NotBlank(message = "用户名不能为空") String username,
    @NotNull(message = "密码不能为空") @Size(min = 6, message = "密码长度至少为6位") String password,
    @NotNull(message = "姓名不能为空") @Size(max = 6, message = "姓名长度最多6位") String name
) {
}

record UserState(
    Long userId,
    String username,
    String name,
    boolean superAdmin,
    boolean admin
) {
    public static UserState fromEntity(com.nytdacm.oa.model.entity.User user) {
        return new UserState(
            user.getUserId(),
            user.getUsername(),
            user.getName(),
            user.getSuperAdmin(),
            user.getAdmin()
        );
    }
}
