package com.nytdacm.oa.service;

import com.nytdacm.oa.model.entity.User;

import java.util.List;

public interface UserService {
    User newUser(User newUser);

    User getUserByUsername(String username);

    User getUserById(Long id);

    User updateUser(User user);

    long count(String username, String name, Boolean active, Boolean admin, Boolean superAdmin);

    long count();

    List<User> getAllUsers(String username, String name, Boolean active, Boolean admin, Boolean superAdmin, int page, int size);
}
