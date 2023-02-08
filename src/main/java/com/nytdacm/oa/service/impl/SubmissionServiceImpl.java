package com.nytdacm.oa.service.impl;

import com.nytdacm.oa.dao.GroupDao;
import com.nytdacm.oa.dao.SubmissionDao;
import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.exception.OaBaseException;
import com.nytdacm.oa.model.entity.Submission;
import com.nytdacm.oa.service.SubmissionService;
import jakarta.inject.Inject;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionDao submissionDao;
    private final UserDao userDao;
    private final GroupDao groupDao;

    @Inject
    public SubmissionServiceImpl(SubmissionDao submissionDao, UserDao userDao, GroupDao groupDao) {
        this.submissionDao = submissionDao;
        this.userDao = userDao;
        this.groupDao = groupDao;
    }

    private Example<Submission> paramsToExample(Long user, String oj) {
        var probe = new Submission();
        if (user != null) {
            var userEntity = userDao.findById(user).orElseThrow(() -> new OaBaseException("用户不存在", 404));
            probe.setUser(userEntity);
        }
        probe.setOj(oj);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase();
        return Example.of(probe, matcher);
    }

    @Override
    public List<Submission> getAllSubmissions(Long user, Long group, String oj, Integer page, Integer size) {
        var sort = Sort.by(Sort.Direction.DESC, "submitTime");
        if (group != null) {
            var g = groupDao.findById(group).orElseThrow(() -> new OaBaseException("群组不存在", 404));
            var users = g.getUsers();
            if (oj == null) {
                oj = "";
            }
            return submissionDao
                .findAllByUserInAndOjContaining(users.stream().toList(), oj, PageRequest.of(page, size, sort)).getContent();
        } else {
            var example = paramsToExample(user, oj);
            return submissionDao.findAll(example, PageRequest.of(page, size, sort)).getContent();
        }
    }

    @Override
    public long count(Long user, Long group, String oj) {
        if (group != null) {
            var g = groupDao.findById(group).orElseThrow(() -> new OaBaseException("群组不存在", 404));
            var users = g.getUsers();
            if (oj == null) {
                oj = "";
            }
            return submissionDao.countByUserInAndOjContaining(users.stream().toList(), oj);
        } else {
            var example = paramsToExample(user, oj);
            return submissionDao.count(example);
        }
    }
}
