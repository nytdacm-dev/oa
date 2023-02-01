package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.nytdacm.oa.model.entity.SocialAccount;
import com.nytdacm.oa.model.entity.User;
import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.ListWrapper;
import com.nytdacm.oa.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Objects;

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
    public ResponseEntity<HttpResponse<ListWrapper<AdminUserDto>>> users(
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
        return HttpResponse.success(200, "获取成功", new ListWrapper<>(count, users));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpResponse<AdminUserDto>> updateUser(
        @PathVariable Long id, @RequestBody @Valid AdminUserUpdateRequest adminUserUpdateRequest) {
        var user = userService.getUserById(id);
        if (StringUtils.isNotEmpty(adminUserUpdateRequest.name())) {
            user.setName(adminUserUpdateRequest.name());
        }
        if (StringUtils.isNotEmpty(adminUserUpdateRequest.password())) {
            user.setPassword(adminUserUpdateRequest.password());
        }
        if (Objects.nonNull(adminUserUpdateRequest.admin())) {
            user.setAdmin(adminUserUpdateRequest.admin());
        }
        if (Objects.nonNull(adminUserUpdateRequest.superAdmin())) {
            user.setSuperAdmin(adminUserUpdateRequest.superAdmin());
        }
        if (Objects.nonNull(adminUserUpdateRequest.active())) {
            user.setActive(adminUserUpdateRequest.active());
        }
        return HttpResponse.success(200, "修改成功", AdminUserDto.fromEntity(userService.updateUser(user)));
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
            user.getAdmin(),
            user.getActive(),
            user.getSocialAccount(),
            user.getCreatedAt()
        );
    }
}

record AdminUserUpdateRequest(
    @Size(min = 6, message = "密码长度至少为6位") String password,
    @Size(max = 6, message = "姓名长度不能超过6位") String name,
    Boolean superAdmin,
    Boolean admin,
    Boolean active
) {
}
