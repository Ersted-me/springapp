package com.ersted.springapp.service;

import com.ersted.springapp.model.User;

import java.util.List;

public interface UserService {
    User register(User user);

    User findById(Long id);

    User findByUsername(String login);

    User update(User user);

    boolean deleteById(Long id);

    boolean deleteByLogin(String login);

    List<User> getAll();
}
