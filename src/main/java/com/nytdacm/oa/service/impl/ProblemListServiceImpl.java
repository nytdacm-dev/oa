package com.nytdacm.oa.service.impl;

import com.nytdacm.oa.dao.ProblemListDao;
import com.nytdacm.oa.service.ProblemListService;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;

@Service
public class ProblemListServiceImpl implements ProblemListService {
    private final ProblemListDao problemListDao;

    @Inject
    public ProblemListServiceImpl(ProblemListDao problemListDao) {
        this.problemListDao = problemListDao;
    }
}
