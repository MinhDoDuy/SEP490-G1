package com.ffood.g1.service;

import com.ffood.g1.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    User register(User user);
    User login(String email, String password);
}