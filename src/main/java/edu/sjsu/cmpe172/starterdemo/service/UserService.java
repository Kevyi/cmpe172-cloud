package edu.sjsu.cmpe172.starterdemo.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import edu.sjsu.cmpe172.starterdemo.mapper.UserMapper;
import edu.sjsu.cmpe172.starterdemo.model.User;

@Service
public class UserService {

    private static final Logger log = Logger.getLogger(UserService.class.getName());
    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public boolean register(User user) {
        if (userMapper.findByEmail(user.getEmail()).isPresent()) {
            log.warning("Registration failed — email already exists: " + user.getEmail());
            return false;
        }
        user.setCreated_at(LocalDate.now());
        int rows = userMapper.insert(user);
        if (rows > 0) log.info("User registered: " + user.getEmail());
        return rows > 0;
    }

    public Optional<User> authenticate(String email, String password) {
        return userMapper.findByEmail(email)
            .filter(u -> u.getPassword().equals(password));
    }

    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
}
