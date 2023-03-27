package com.nytdacm.oa.controller.admin

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.nytdacm.oa.controller.ArticleDto
import com.nytdacm.oa.response.HttpResponse
import com.nytdacm.oa.response.ListWrapper
import com.nytdacm.oa.service.ArticleService
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
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

    @DeleteMapping("/{id}")
    fun deleteArticle(@PathVariable id: Long): ResponseEntity<HttpResponse<Void>> {
        articleService.delete(id)
        return HttpResponse.success(200, "删除成功", null)
    }

    @PatchMapping("/{id}")
    fun updateArticle(
        @PathVariable id: Long,
        @RequestBody @Valid
        request: AdminArticleUpdateRequest,
    ): ResponseEntity<HttpResponse<ArticleDto>> {
        val article = articleService.getArticle(id)
        if (request.title != null) {
            article.title = request.title
        }
        if (request.content != null) {
            article.content = request.content
        }
        if (request.published != null) {
            article.published = request.published
        }
        return HttpResponse.success(200, "更新成功", ArticleDto.toDto(articleService.update(article)))
    }
}

data class AdminArticleUpdateRequest(
    @Size(min = 1, message = "标题不能为空") val title: String?,
    @Size(min = 1, message = "内容不能为空") val content: String?,
    val published: Boolean?,
)
