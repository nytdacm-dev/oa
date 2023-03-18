package com.nytdacm.oa.service

import com.nytdacm.oa.dao.ArticleDao
import com.nytdacm.oa.entity.Article
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ArticleServiceImpl(
    private val articleDao: ArticleDao
) : ArticleService {
    override fun newArticle(article: Article): Article = articleDao.save(article)

    override fun getAllArticles(title: String, page: Int, size: Int): List<Article> {
        val sort = Sort.by(Sort.Direction.DESC, "articleId")
        return articleDao.findAll(
            { root, query, criteriaBuilder -> criteriaBuilder.like(root.get("title"), "%$title%") },
            PageRequest.of(page, size, sort)
        ).content
    }

    override fun count(title: String): Long {
        return articleDao.count { root, query, criteriaBuilder -> criteriaBuilder.like(root.get("title"), "%$title%") }
    }
}
