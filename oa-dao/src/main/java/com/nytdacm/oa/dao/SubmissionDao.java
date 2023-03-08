package com.nytdacm.oa.dao;

import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SubmissionDao extends JpaRepository<Submission, Long>, JpaSpecificationExecutor<Submission> {
    List<Submission> findAllByUser(User user);
}
