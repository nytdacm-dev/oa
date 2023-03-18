package com.nytdacm.oa.dao

import com.nytdacm.oa.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ArticleDao : JpaRepository<Article, Long>, JpaSpecificationExecutor<Article>
