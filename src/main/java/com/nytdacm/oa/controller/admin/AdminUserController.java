package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.nytdacm.oa.model.entity.SocialAccount;
import com.nytdacm.oa.model.entity.User;
import com.nytdacm.oa.model.response.ListWrapper;
import com.nytdacm.oa.service.UserService;
import jakarta.inject.Inject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/admin/user")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super-admin"}, mode = SaMode.OR)
public class AdminUserController {
    private final UserService userService;

    @Inject
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ListWrapper<AdminUserDto>> users(
        // TODO: 存在未登录时可以访问的 bug
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean active,
        @RequestParam(required = false) Boolean admin,
        @RequestParam(required = false) Boolean superAdmin,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var users = userService.getAllUsers(username, name, active, admin, superAdmin, page, size)
            .stream().map(AdminUserDto::fromEntity).toList();
        var count = userService.count(username, name, active, admin, superAdmin);
        return ResponseEntity.ok(new ListWrapper<>(count, users));
    }
}

record AdminUserDto(
    Long userId,
    String username,
    String name,
    Boolean superAdmin,
    Boolean admin,
    Boolean active,
    SocialAccount socialAccount,
    Instant registerTime
) {
    public static AdminUserDto fromEntity(User user) {
        return new AdminUserDto(
            user.getUserId(),
            user.getUsername(),
            user.getName(),
            user.getSuperAdmin(),
            user.getActive(),
            user.getActive(),
            user.getSocialAccount(),
            user.getCreatedAt()
        );
    }
}
