package com.nytdacm.oa.service.impl;

import com.nytdacm.oa.dao.UserDao;
import com.nytdacm.oa.exception.OaBaseException;
import com.nytdacm.oa.model.entity.User;
import com.nytdacm.oa.service.UserService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Inject
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
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
    public long count() {
        return userDao.count();
    }
}
