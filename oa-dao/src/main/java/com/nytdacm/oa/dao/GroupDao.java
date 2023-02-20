package com.nytdacm.oa.dao;

import com.nytdacm.oa.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupDao extends JpaRepository<Group, Long> {
}
