package com.nytdacm.oa.service

import com.nytdacm.oa.entity.Article

interface ArticleService {
    fun newArticle(article: Article): Article

    fun getAllArticles(title: String, page: Int, size: Int): List<Article>
    fun count(title: String): Long
}
