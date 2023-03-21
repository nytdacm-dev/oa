package com.nytdacm.oa.service

import com.nytdacm.oa.entity.Article

interface ArticleService {
    fun newArticle(article: Article): Article

    fun getArticle(id: Long): Article
    fun getAllArticles(title: String, published: Boolean?, page: Int, size: Int): List<Article>
    fun count(title: String, published: Boolean?): Long
}
