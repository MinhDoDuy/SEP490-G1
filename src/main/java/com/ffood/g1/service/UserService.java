package com.ffood.g1.service;

import com.ffood.g1.dto.UserDTO;
import com.ffood.g1.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User loadUserById(Integer userId);

    void updateUser(User user);

    boolean isEmailExist(String email);


    void registerNewUser(User user);

    void saveUserWithDefaultRole(User user);

    boolean isCodeNameExist(String codeName);


//    boolean isPhoneValid(String );
}