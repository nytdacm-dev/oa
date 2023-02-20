package com.nytdacm.oa.service;

import com.nytdacm.oa.entity.Submission;

import java.util.List;

public interface SubmissionService {
    List<Submission> getAllSubmissions(String user, Long group, String oj, Integer page, Integer size);

    long count(String user, Long group, String oj);
}
