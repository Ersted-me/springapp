package com.ersted.springapp.service.impl;

import com.ersted.springapp.model.Role;
import com.ersted.springapp.model.Status;
import com.ersted.springapp.model.User;
import com.ersted.springapp.repository.RoleRepository;
import com.ersted.springapp.repository.UserRepository;
import com.ersted.springapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {
        User existUser = userRepository.findUserByLogin(user.getLogin());

        if(existUser != null){
            log.warn("IN UserServiceImpl:register - user with login: {} already exist", user.getLogin());
            return null;
        }

        Role role = roleRepository.findRoleByName("ROLE_USER");
        List<Role> roles = Collections.singletonList(role);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);
        user.setStatus(Status.ACTIVE);

        User createdUser = userRepository.save(user);

        log.info("IN UserServiceImpl:register - user: {} successfully created", createdUser);

        return createdUser;
    }

    @Override
    public User findById(Long id) {
        User result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN UserServiceImpl:findById - no user found by id: {}", id);
            return null;
        }

        log.info("IN UserServiceImpl:findById - user: {} found by id: {}", result, id);
        return result;
    }

    @Override
    public User findByUsername(String login){
        User result = userRepository.findUserByLogin(login);

        if (result == null) {
            log.warn("IN UserServiceImpl:findByLogin - no user found by login: {}", login);
            return null;
        }

        log.info("IN UserServiceImpl:findByLogin - user: {} found by login: {}", result, login);
        return result;
    }

    @Override
    public User update(User user) {
        User current = userRepository.findById(user.getId()).orElse(null);

        if (current == null) {
            log.warn("IN UserServiceImpl:update - user: {} not found", user);
            return null;
        }

        current.setFirstName(user.getFirstName());
        current.setLastName(user.getLastName());
        current.setEmail(user.getEmail());
        current.setLogin(user.getLogin());
        User updated = userRepository.save(current);

        log.info("IN UserServiceImpl:update - updatedUser: {} ", updated);
        return updated;
    }

    @Override
    public boolean deleteById(Long id) {
        User active = userRepository.findById(id).orElse(null);

        if (active == null) {
            log.warn("IN UserServiceImpl:deleteById - user with id: {} not found", id);
            return false;
        }
        active.setStatus(Status.DELETED);

        User deleted = userRepository.save(active);

        log.info("IN UserServiceImpl:deleteById - user: {} successfully deleted", deleted);
        return true;
    }

    @Override
    public boolean deleteByLogin(String login){
        User active = userRepository.findUserByLogin(login);

        if (active == null) {
            log.warn("IN UserServiceImpl:deleteByLogin - user with login: {} not found", login);
            return false;
        }

        active.setStatus(Status.DELETED);
        User deleted = userRepository.save(active);

        log.info("IN UserServiceImpl:deleteByLogin - user: {} successfully deleted", deleted);
        return true;
    }

    @Override
    public List<User> getAll() {
        List<User> result = userRepository.findAll();

        log.info("IN UserServiceImpl:getAll - {} users found", result.size());

        return result;
    }
}
