package com.ffood.g1.service;

import com.ffood.g1.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    //get thông tin của 1 user thông qua mail
    User findByEmail(String email);
    //lấy ra thông tin của user
    User loadUserById(Integer userId);
    //edit profile user
    void updateUser(User user);
    //update password
    void updatePassword(User user, String newPassword);





}