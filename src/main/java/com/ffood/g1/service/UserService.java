package com.ffood.g1.service;

import com.ffood.g1.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {

    User findByEmail(String email);

////
//    User loadUserById(Integer userId);
////
//void updateUser(User user);
//
//
//    void save(User user);
//    void changePassword(Integer userId, String newPassword);
}