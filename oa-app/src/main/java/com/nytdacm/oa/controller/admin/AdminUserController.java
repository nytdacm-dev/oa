package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.nytdacm.oa.entity.Group;
import com.nytdacm.oa.entity.SocialAccount;
import com.nytdacm.oa.entity.User;
import com.nytdacm.oa.response.HttpResponse;
import com.nytdacm.oa.response.ListWrapper;
import com.nytdacm.oa.service.GroupService;
import com.nytdacm.oa.service.UserService;
import com.nytdacm.oa.utils.PasswordUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/user")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super-admin"}, mode = SaMode.OR)
public class AdminUserController {
    private final UserService userService;
    private final GroupService groupService;

    public AdminUserController(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<HttpResponse<ListWrapper<AdminUserDto>>> users(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean active,
        @RequestParam(required = false) Boolean admin,
        @RequestParam(required = false) Boolean superAdmin,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var users = userService.getAllUsers(username, name, active, admin, superAdmin, page, size)
            .parallelStream().map(AdminUserDto::fromEntity).toList();
        var count = userService.count(username, name, active, admin, superAdmin);
        return HttpResponse.success(200, "获取成功", new ListWrapper<>(count, users));
    }

    @GetMapping("/{id}")
    @Cacheable(value = "user", key = "#id")
    public ResponseEntity<HttpResponse<AdminUserDto>> getUser(@PathVariable Long id) {
        var user = userService.getUserById(id);
        return HttpResponse.success(200, "获取成功", AdminUserDto.fromEntity(user));
    }

    @GetMapping("/{id}/groups")
    public ResponseEntity<HttpResponse<List<Long>>> getUserGroups(@PathVariable Long id) {
        var groups = userService.getUserById(id).getGroups().parallelStream().map(Group::getGroupId).toList();
        return HttpResponse.success(200, "获取成功", groups);
    }

    @PostMapping("/{id}/groups")
    public ResponseEntity<HttpResponse<Void>> setUserGroups(
        @PathVariable Long id,
        @RequestBody UserGroupRequest userGroupRequest
    ) {
        var user = userService.getUserById(id);
        var groups = userGroupRequest.groups().parallelStream().map(groupService::getGroupById).collect(Collectors.toSet());
        user.setGroups(groups);
        userService.updateUser(user);
        return HttpResponse.success(200, "修改成功", null);
    }

    @PatchMapping("/{id}")
    @CacheEvict(value = "user", key = "#id")
    public ResponseEntity<HttpResponse<AdminUserDto>> updateUser(
        @PathVariable Long id, @RequestBody @Valid AdminUserUpdateRequest adminUserUpdateRequest) {
        var user = userService.getUserById(id);
        if (StringUtils.isNotEmpty(adminUserUpdateRequest.name())) {
            user.setName(adminUserUpdateRequest.name());
        }
        if (StringUtils.isNotEmpty(adminUserUpdateRequest.password())) {
            var salt = PasswordUtil.getSalt();
            user.setPasswordSalt(salt);
            user.setPassword(PasswordUtil.hashPassword(adminUserUpdateRequest.password(), salt));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse<Void>> deleteUser(@PathVariable Long id) {
        if (id.equals(StpUtil.getLoginIdAsLong())) {
            return HttpResponse.fail(400, "不能删除自己", null);
        }
        userService.delete(id);
        return HttpResponse.success(200, "删除成功", null);
    }
}

record UserGroupRequest(
    List<Long> groups
) {
}

record AdminUserDto(
    Long userId,
    String username,
    String name,
    Boolean superAdmin,
    Boolean admin,
    Boolean active,
    SocialAccount socialAccount,
    List<GroupDto> groups,
    Instant registerTime,
    Instant lastActive
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
            user.getGroups().parallelStream().map(GroupDto::fromEntity).toList(),
            user.getCreatedAt(),
            user.getLastActive()
        );
    }

    record GroupDto(
        Long groupId,
        String name,
        Boolean showInHomepage,
        Instant createdAt
    ) {
        public static GroupDto fromEntity(Group group) {
            return new GroupDto(
                group.getGroupId(),
                group.getName(),
                group.getShowInHomepage(),
                group.getCreatedAt()
            );
        }
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
