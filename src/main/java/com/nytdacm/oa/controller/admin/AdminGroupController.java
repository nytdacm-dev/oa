package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.nytdacm.oa.model.entity.Group;
import com.nytdacm.oa.model.entity.User;
import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.ListWrapper;
import com.nytdacm.oa.service.GroupService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/admin/group")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super-admin"}, mode = SaMode.OR)
public class AdminGroupController {
    private final GroupService groupService;

    @Inject
    public AdminGroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<HttpResponse<ListWrapper<AdminGroupDto>>> groups(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean showInHomePage,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var groups = groupService.getAllGroups(name, showInHomePage, page, size).stream()
            .map(AdminGroupDto::fromEntity).toList();
        return HttpResponse.success(200, "获取成功",
            new ListWrapper<>(groupService.count(name, showInHomePage), groups));
    }

    @PostMapping
    public ResponseEntity<HttpResponse<AdminGroupDto>> newGroup(@Valid @RequestBody NewGroupRequest newGroupRequest) {
        var group = new Group();
        group.setName(newGroupRequest.name());
        group = groupService.newGroup(group);
        return HttpResponse.success(200, "创建成功", AdminGroupDto.fromEntity(group));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse<Void>> deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
        return HttpResponse.success(200, "删除成功", null);
    }
}

record NewGroupRequest(
    @NotNull(message = "名称不能为空") String name
) {
}

record AdminGroupDto(
    Long groupId,
    String name,
    List<UserDto> users,
    Boolean showInHomepage,
    Instant createdAt
) {
    public static AdminGroupDto fromEntity(Group group) {
        return new AdminGroupDto(
            group.getGroupId(),
            group.getName(),
            group.getUsers().stream().map(UserDto::fromEntity).toList(),
            group.getShowInHomepage(),
            group.getCreatedAt()
        );
    }

    private record UserDto(
        Long userId,
        String username,
        String name,
        Instant registerTime
    ) {
        public static UserDto fromEntity(User user) {
            return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getCreatedAt()
            );
        }
    }
}
