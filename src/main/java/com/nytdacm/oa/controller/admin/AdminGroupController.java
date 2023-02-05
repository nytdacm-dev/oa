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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "2147483647") Integer size
    ) {
        var groups = groupService.getAllGroups(name, page, size).stream()
            .map(GroupDto::fromEntity).toList();
        return HttpResponse.success(200, "获取成功",
            new ListWrapper<>(groupService.count(name), groups));
    }

    @PostMapping
    public ResponseEntity<HttpResponse<GroupDto>> newGroup(@Valid @RequestBody NewGroupRequest newGroupRequest) {
        var group = new Group();
        group.setName(newGroupRequest.name());
        group = groupService.newGroup(group);
        return HttpResponse.success(200, "创建成功", GroupDto.fromEntity(group));
    }
}

record NewGroupRequest(
    @NotNull(message = "名称不能为空") String name
) {
}
