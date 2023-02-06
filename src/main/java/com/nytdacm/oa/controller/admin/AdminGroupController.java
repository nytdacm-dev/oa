package com.nytdacm.oa.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import com.nytdacm.oa.model.entity.Group;
import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.ListWrapper;
import com.nytdacm.oa.model.response.group.GroupDto;
import com.nytdacm.oa.service.GroupService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<HttpResponse<ListWrapper<GroupDto>>> groups(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean showInHomePage,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var groups = groupService.getAllGroups(name, showInHomePage, page, size).stream()
            .map(GroupDto::fromEntity).toList();
        return HttpResponse.success(200, "获取成功",
            new ListWrapper<>(groupService.count(name, showInHomePage), groups));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse<GroupDto>> group(@PathVariable Long id) {
        var group = groupService.getGroupById(id);
        return HttpResponse.success(200, "获取成功", GroupDto.fromEntity(group));
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
    Boolean showInHomepage
) {
}
