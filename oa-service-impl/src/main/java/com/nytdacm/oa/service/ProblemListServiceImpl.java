package com.nytdacm.oa.service;

import com.nytdacm.oa.dao.ProblemListDao;
import org.springframework.stereotype.Service;

@Service
public class ProblemListServiceImpl implements ProblemListService {
    private final ProblemListDao problemListDao;

    public ProblemListServiceImpl(ProblemListDao problemListDao) {
        this.problemListDao = problemListDao;
    }
}
