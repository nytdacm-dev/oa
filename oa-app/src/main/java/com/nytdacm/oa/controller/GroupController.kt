package com.nytdacm.oa.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import com.nytdacm.oa.response.HttpResponse
import com.nytdacm.oa.response.group.GroupDto
import com.nytdacm.oa.service.GroupService
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/group")
@SaCheckLogin
open class GroupController(
    private val groupService: GroupService,
) {
    @GetMapping("/homepage")
    @Cacheable(value = ["homepageGroups"], key = "#root.methodName")
    open fun homepageGroups(): ResponseEntity<HttpResponse<List<GroupDto>>> {
        val groups = groupService.getAllGroups(true).map { GroupDto.fromEntity(it) }
        return HttpResponse.success(200, "获取成功", groups)
    }
}
