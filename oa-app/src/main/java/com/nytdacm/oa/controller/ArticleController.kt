package com.nytdacm.oa.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.nytdacm.oa.entity.Article
import com.nytdacm.oa.entity.User
import com.nytdacm.oa.response.HttpResponse
import com.nytdacm.oa.response.ListWrapper
import com.nytdacm.oa.response.user.UserDto
import com.nytdacm.oa.service.ArticleService
import com.nytdacm.oa.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/article")
class ArticleController(
    private val articleService: ArticleService,
    private val userService: UserService,
) {
    @GetMapping
    fun articles(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "2147483647") size: Int,
    ): ResponseEntity<HttpResponse<ListWrapper<ArticleDto>>>? {
        val list = articleService.getAllArticles(title ?: "", true, page, size).map {
            ArticleDto.toDto(it)
        }
        val cnt = articleService.count(title ?: "", true)
        return HttpResponse.success(200, "获取成功", ListWrapper(cnt, list))
    }

    @GetMapping("/{id}")
    fun article(
        @PathVariable(value = "id", required = true) id: Long,
    ): ResponseEntity<HttpResponse<ArticleDto>> {
        return HttpResponse.success(200, "获取成功", articleService.getArticle(id).let { ArticleDto.toDto(it) })
    }

    @PostMapping
    @SaCheckLogin
    fun writeArticle(
        @RequestBody(required = true) newArticleRequest: NewArticleRequest,
    ): ResponseEntity<HttpResponse<ArticleDto>> {
        val id = StpUtil.getLoginIdAsLong()
        val user: User = userService.getUserById(id)
        val article = Article(newArticleRequest.title, newArticleRequest.content, user)
        return HttpResponse.success(
            200,
            "发表成功",
            articleService.newArticle(article).let { ArticleDto.toDto(it) },
        )
    }
}

data class ArticleDto(
    val articleId: Long?,
    val title: String,
    val content: String,
    val author: UserDto,
    val published: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant?,
) {
    companion object {
        fun toDto(article: Article) =
            ArticleDto(
                article.articleId,
                article.title,
                article.content,
                UserDto.fromEntity(article.author),
                article.published,
                article.createdAt,
                article.updatedAt,
            )
    }
}

data class NewArticleRequest(
    val title: String,
    val content: String,
)
