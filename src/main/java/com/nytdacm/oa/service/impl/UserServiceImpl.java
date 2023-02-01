package com.nytdacm.oa.service.impl;

import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.exception.OaBaseException;
import com.nytdacm.oa.model.entity.User;
import com.nytdacm.oa.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Inject
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    private Example<User> paramsToExample(String username, String name, Boolean active, Boolean admin, Boolean superAdmin) {
        var probe = new User();
        probe.setUsername(username);
        probe.setName(name);
        probe.setActive(active);
        probe.setAdmin(admin);
        probe.setSuperAdmin(superAdmin);
        probe.setSocialAccount(null);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withIgnoreNullValues()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase();
        return Example.of(probe, matcher);
    }

    @Override
    public User newUser(User newUser) {
        if (userDao.existsByUsername(newUser.getUsername())) {
            throw new OaBaseException("用户已存在", 409);
        }
        return userDao.save(newUser);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new OaBaseException("用户不存在", 404));
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findById(id).orElseThrow(() -> new OaBaseException("用户不存在", 404));
    }

    @Override
    public User updateUser(User user) {
        return userDao.save(user);
    }

    @Override
    public long count(String username, String name, Boolean active, Boolean admin, Boolean superAdmin) {
        var example = paramsToExample(username, name, active, admin, superAdmin);
        return userDao.count(example);
    }

    @Override
    public long count() {
        return count(null, null, null, null, null);
    }

    @Override
    public List<User> getAllUsers(String username, String name, Boolean active, Boolean admin, Boolean superAdmin, int page, int size) {
        var example = paramsToExample(username, name, active, admin, superAdmin);
        var sort = Sort.by(Sort.Direction.ASC, "userId");
        return userDao.findAll(example, PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public void delete(Long id) {
        userDao.deleteById(id);
    }
}
