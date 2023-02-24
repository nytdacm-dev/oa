package com.nytdacm.oa.dao

import com.nytdacm.oa.entity.ProblemList
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemListDao : JpaRepository<ProblemList, Long>
