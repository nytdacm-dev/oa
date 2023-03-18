package com.nytdacm.oa.controller

import com.nytdacm.oa.entity.Article
import com.nytdacm.oa.response.HttpResponse
import com.nytdacm.oa.response.ListWrapper
import com.nytdacm.oa.response.user.UserDto
import com.nytdacm.oa.service.ArticleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping
    fun articles(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "2147483647") size: Int,
    ): ResponseEntity<HttpResponse<ListWrapper<ArticleDto>>>? {
        val list = articleService.getAllArticles(title ?: "", page, size).map {
            ArticleDto(it.articleId, it.title, it.content, UserDto.fromEntity(it.author))
        }
        val cnt = articleService.count(title ?: "")
        return HttpResponse.success(200, "获取成功", ListWrapper(cnt, list))
    }

    @GetMapping("/{id}")
    fun article(
        @PathVariable(value = "id", required = true) id: Long,
    ): ResponseEntity<HttpResponse<ArticleDto>> {
        return HttpResponse.success(200, "获取成功", articleService.getArticle(id).let { ArticleDto.toDto(it) })
    }
}

data class ArticleDto(
    val articleId: Long?,
    val title: String,
    val content: String,
    val author: UserDto,
) {
    companion object {
        fun toDto(article: Article) =
            ArticleDto(article.articleId, article.title, article.content, UserDto.fromEntity(article.author))
    }
}
