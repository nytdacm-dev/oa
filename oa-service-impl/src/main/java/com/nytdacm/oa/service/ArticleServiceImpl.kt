package com.nytdacm.oa.service

import com.nytdacm.oa.dao.ArticleDao
import com.nytdacm.oa.entity.Article
import com.nytdacm.oa.exception.OaBaseException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ArticleServiceImpl(
    private val articleDao: ArticleDao,
) : ArticleService {
    override fun newArticle(article: Article): Article = articleDao.save(article)
    override fun getArticle(id: Long): Article {
        return articleDao.findById(id).orElseThrow {
            OaBaseException("群组不存在", 404)
        }
    }

    override fun getAllArticles(title: String, page: Int, size: Int): List<Article> {
        val sort = Sort.by(Sort.Direction.DESC, "articleId")
        return articleDao.findAll(
            { root, query, criteriaBuilder -> criteriaBuilder.like(root.get("title"), "%$title%") },
            PageRequest.of(page, size, sort),
        ).content
    }

    override fun count(title: String): Long {
        return articleDao.count { root, query, criteriaBuilder -> criteriaBuilder.like(root.get("title"), "%$title%") }
    }
}
