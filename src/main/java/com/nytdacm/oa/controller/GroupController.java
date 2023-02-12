package com.nytdacm.oa.controller;

import com.nytdacm.oa.model.response.HttpResponse;
import com.nytdacm.oa.model.response.group.GroupDto;
import com.nytdacm.oa.service.GroupService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/homepage")
    @Cacheable(value = "homepageGroups", key = "#root.methodName")
    public ResponseEntity<HttpResponse<List<GroupDto>>> homepageGroups() {
        var groups = groupService.getAllGroups(true).parallelStream().map(GroupDto::fromEntity).toList();
        return HttpResponse.success(200, "获取成功", groups);
    }
}
