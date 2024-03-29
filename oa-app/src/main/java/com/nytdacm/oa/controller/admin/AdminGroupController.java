package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.nytdacm.oa.entity.Group;
import com.nytdacm.oa.entity.User;
import com.nytdacm.oa.response.HttpResponse;
import com.nytdacm.oa.response.ListWrapper;
import com.nytdacm.oa.response.group.GroupDto;
import com.nytdacm.oa.service.GroupService;
import com.nytdacm.oa.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/group")
@SaCheckLogin
@SaCheckRole(value = {"admin", "super-admin"}, mode = SaMode.OR)
public class AdminGroupController {
    private final GroupService groupService;
    private final UserService userService;

    public AdminGroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<HttpResponse<ListWrapper<GroupDto>>> groups(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean showInHomePage,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var groups = groupService.getAllGroups(name, showInHomePage, page, size).parallelStream()
            .map(GroupDto::fromEntity).toList();
        return HttpResponse.success(200, "获取成功",
            new ListWrapper<>(groupService.count(name, showInHomePage), groups));
    }

    @GetMapping("/{id}")
    @Cacheable(value = "group", key = "#id")
    public ResponseEntity<HttpResponse<GroupDto>> group(@PathVariable Long id) {
        var group = groupService.getGroupById(id);
        return HttpResponse.success(200, "获取成功", GroupDto.fromEntity(group));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<HttpResponse<List<Long>>> getGroupMembers(@PathVariable Long id) {
        var members = groupService.getGroupById(id).getUsers().stream()
            .map(User::getUserId)
            .toList();
        return HttpResponse.success(200, "获取成功", members);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<HttpResponse<Void>> setUserGroups(
        @PathVariable Long id,
        @RequestBody GroupMemberRequest groupMemberRequest
    ) {
        var group = groupService.getGroupById(id);
        var members = groupMemberRequest.members().parallelStream().map(userService::getUserById).collect(Collectors.toSet());
        group.setUsers(members);
        groupService.updateGroup(group);
        return HttpResponse.success(200, "修改成功", null);
    }

    private record GroupMemberRequest(
        List<Long> members
    ) {
    }

    @PostMapping
    public ResponseEntity<HttpResponse<GroupDto>> newGroup(@Valid @RequestBody NewGroupRequest newGroupRequest) {
        var group = new Group();
        group.setName(newGroupRequest.name());
        group.setDisplayName(newGroupRequest.displayName());
        group.setShowInHomepage(newGroupRequest.showInHomepage());
        group = groupService.newGroup(group);
        return HttpResponse.success(200, "创建成功", GroupDto.fromEntity(group));
    }

    @PatchMapping("/{id}")
    @CacheEvict(value = "group", key = "#id")
    public ResponseEntity<HttpResponse<Void>> updateGroup(
        @RequestBody @Valid GroupUpdateRequest groupUpdateRequest,
        @PathVariable Long id
    ) {
        var group = groupService.getGroupById(id);
        if (groupUpdateRequest.name() != null) {
            group.setName(groupUpdateRequest.name());
        }
        if (groupUpdateRequest.displayName() != null) {
            group.setDisplayName(groupUpdateRequest.displayName());
        }
        if (groupUpdateRequest.showInHomepage() != null) {
            group.setShowInHomepage(groupUpdateRequest.showInHomepage());
        }
        if (groupUpdateRequest.homepageOrder() != null) {
            group.setHomepageOrder(groupUpdateRequest.homepageOrder());
        }
        groupService.newGroup(group);
        return HttpResponse.success(200, "更新成功", null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse<Void>> deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
        return HttpResponse.success(200, "删除成功", null);
    }
}

record NewGroupRequest(
    @NotNull(message = "名称不能为空") String name,
    String displayName,
    Boolean showInHomepage
) {
}

record GroupUpdateRequest(
    String name,
    String displayName,
    Boolean showInHomepage,
    Integer homepageOrder
) {
}
