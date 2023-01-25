package com.nytdacm.oa.service;

import com.nytdacm.oa.model.entity.User;

public interface UserService {
    User newUser(User newUser);

    User getUserByUsername(String username);

    User getUserById(Long id);

    User updateUser(User user);
}
