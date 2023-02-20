package com.nytdacm.oa.service;

import com.nytdacm.oa.dao.GroupDao;
import com.nytdacm.oa.dao.SubmissionDao;
import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.entity.Submission;
import com.nytdacm.oa.entity.User;
import com.nytdacm.oa.exception.OaBaseException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
            users = userDao.findAllByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(user, user);
        }
        return users;
    }

    @Override
    public List<Submission> getAllSubmissions(String user, Long group, String oj, Integer page, Integer size) {
        var sort = Sort.by(Sort.Direction.DESC, "submitTime");
        if (user == null) {
            user = "";
        }
        if (oj == null) {
            oj = "";
        }
        var users = paramsToUsers(user, group);
        return submissionDao
            .findAllByUserInAndOjContaining(users, oj, PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public long count(String user, Long group, String oj) {
        if (user == null) {
            user = "";
        }
        if (oj == null) {
            oj = "";
        }
        var users = paramsToUsers(user, group);
        return submissionDao.countByUserInAndOjContaining(users, oj);
    }
}
