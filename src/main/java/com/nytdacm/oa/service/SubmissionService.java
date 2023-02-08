package com.nytdacm.oa.service;

import com.nytdacm.oa.model.entity.Submission;

import java.util.List;

public interface SubmissionService {
    List<Submission> getAllSubmissions(Long user, Long group, String oj, Integer page, Integer size);

    long count(Long user, Long group, String oj);
}
