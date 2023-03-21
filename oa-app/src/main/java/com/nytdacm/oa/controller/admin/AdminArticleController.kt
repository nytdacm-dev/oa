package com.nytdacm.oa.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.nytdacm.oa.controller.ArticleDto
import com.nytdacm.oa.response.HttpResponse
import com.nytdacm.oa.response.ListWrapper
import com.nytdacm.oa.service.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/article")
@SaCheckLogin
@SaCheckRole(value = ["admin", "super-admin"], mode = SaMode.OR)
class AdminArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping
    fun getAllArticles(
        @RequestParam(required = false, defaultValue = "") title: String,
        @RequestParam(required = false) published: Boolean?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "2147483647") size: Int,
    ): ResponseEntity<HttpResponse<ListWrapper<ArticleDto>>> {
        val articles = articleService.getAllArticles(title, published, page, size)
            .map { ArticleDto.toDto(it) }
        val count = articleService.count(title, published)
        return HttpResponse.success(200, "获取成功", ListWrapper(count, articles))
    }
}
