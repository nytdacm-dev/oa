package com.nytdacm.oa.dao;

import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionDao extends JpaRepository<Submission, Long> {
    boolean existsByRemoteSubmissionIdAndOjAndUser(String remoteSubmissionId, String oj, User user);

    List<Submission> findAllByUser(User user);

    List<Submission> findAllByUserInAndOjContaining(List<User> users, String oj);

    long countByUserInAndOjContaining(List<User> users, String oj);
}