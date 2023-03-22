package com.nytdacm.oa.service

import com.nytdacm.oa.dao.ArticleDao
import com.nytdacm.oa.entity.Article
import com.nytdacm.oa.exception.OaBaseException
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
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

    override fun getAllArticles(title: String, published: Boolean?, page: Int, size: Int): List<Article> {
        val sort = Sort.by(Sort.Direction.DESC, "articleId")
        return articleDao.findAll(
            buildSpecification(title, published),
            PageRequest.of(page, size, sort),
        ).content
    }

    override fun count(title: String, published: Boolean?): Long {
        return articleDao.count(buildSpecification(title, published))
    }

    override fun delete(id: Long) {
        articleDao.deleteById(id)
    }

    private fun buildSpecification(title: String, published: Boolean?): Specification<Article> {
        return Specification { root, query, criteriaBuilder ->
            val predicateList = mutableListOf<Predicate>()
            predicateList.add(criteriaBuilder.like(root.get("title"), "%$title%"))
            if (published == true) {
                predicateList.add(criteriaBuilder.isTrue(root.get("published")))
            } else if (published == false) {
                predicateList.add(criteriaBuilder.isFalse(root.get("published")))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }
    }
}
