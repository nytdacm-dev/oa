package com.nytdacm.oa.dao;

import com.nytdacm.oa.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAllByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(String username, String name);
}
