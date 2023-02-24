package com.nytdacm.oa.dao

import com.nytdacm.oa.entity.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupDao : JpaRepository<Group, Long>
