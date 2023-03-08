package com.nytdacm.oa.service;

import com.nytdacm.oa.dao.GroupDao;
import com.nytdacm.oa.dao.SubmissionDao;
import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.entity.User;
import com.nytdacm.oa.exception.OaBaseException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionDao submissionDao;
    private final UserDao userDao;
    private final GroupDao groupDao;

    public SubmissionServiceImpl(SubmissionDao submissionDao, UserDao userDao, GroupDao groupDao) {
        this.submissionDao = submissionDao;
        this.userDao = userDao;
        this.groupDao = groupDao;
    }

    private List<User> paramsToUsers(String user, Long group) {
        List<User> users;
        if (group != null) {
            var g = groupDao.findById(group).orElseThrow(() -> new OaBaseException("群组不存在", 404));
            users = g.getUsers().stream()
                .filter(u ->
                    u.getUsername().toLowerCase().contains(user.toLowerCase()) ||
                        u.getName().toLowerCase().contains(user.toLowerCase()))
                .toList();
        } else {
            users = userDao.findAll((Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                String u = user.toLowerCase();
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + u + "%"));
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + u + "%"));
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            });
        }
        return users;
    }

    @Override
    public List<Submission> getAllSubmissions(String user, Long group, String oj, Integer page, Integer size) {
        var sort = Sort.by(Sort.Direction.DESC, "submitTime");
        if (user == null) {
            user = "";
        }
        var users = paramsToUsers(user, group);
        return submissionDao.findAll(buildSpecification(users, oj), PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public long count(String user, Long group, String oj) {
        if (user == null) {
            user = "";
        }
        var users = paramsToUsers(user, group);
        return submissionDao.count(buildSpecification(users, oj));
    }

    private Specification<Submission> buildSpecification(List<User> users, String oj) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.in(root.get("user")).value(users));
            if (oj != null) {
                predicateList.add(criteriaBuilder.like(root.get("oj"), "%" + oj + "%"));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
