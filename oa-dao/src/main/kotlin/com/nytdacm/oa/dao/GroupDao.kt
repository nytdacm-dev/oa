package com.nytdacm.oa.dao

import com.nytdacm.oa.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface GroupDao : JpaRepository<Group, Long>, JpaSpecificationExecutor<Group>
