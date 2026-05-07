package edu.sjsu.cmpe172.starterdemo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.sjsu.cmpe172.starterdemo.mapper.AdminMapper;
import edu.sjsu.cmpe172.starterdemo.model.Admin;

@Service
public class AdminService {

    private final AdminMapper adminMapper;

    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public Optional<Admin> authenticate(String email, String password) {
        return adminMapper.findByEmail(email)
            .filter(a -> a.getPassword().equals(password));
    }
}
