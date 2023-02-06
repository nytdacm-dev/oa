package com.nytdacm.oa.dao;

import com.nytdacm.oa.model.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionDao extends JpaRepository<Submission, Long> {
    boolean existsByRemoteSubmissionIdAndOj(String remoteSubmissionId, String oj);
}
