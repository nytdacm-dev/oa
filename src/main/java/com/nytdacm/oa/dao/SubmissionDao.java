package com.nytdacm.oa.dao;

import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionDao extends JpaRepository<Submission, Long> {
    boolean existsByRemoteSubmissionIdAndOjAndUser(String remoteSubmissionId, String oj, User user);

    List<Submission> findAllByUser(User user);

    Page<Submission> findAllByUserInAndOjContaining(List<User> users, String oj, Pageable pageable);

    long countByUserInAndOjContaining(List<User> users, String oj);
}
