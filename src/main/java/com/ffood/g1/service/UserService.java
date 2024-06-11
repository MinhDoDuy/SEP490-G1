package com.ffood.g1.service;

import com.ffood.g1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;


public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User loadUserById(Integer userId);

    boolean isPhoneExist(String phone);

    //edit profile user
    void updateUser(User user);

    boolean isEmailExist(String email);

    void registerNewUser(User user);

    void saveUserWithDefaultRole(User user);

    boolean isCodeNameExist(String codeName);

    //forgot pass
    void sendResetPasswordEmail(String email, HttpServletRequest request);
    boolean isResetTokenValid(String token);
    void updatePasswordReset(String token, String password);

    //change pass
    void updatePassword(User user, String newPassword);


    //List người dùng cho admin đã có phân trang
    Page<User> getAllUsers(int page, int size);

    //search user
    Page<User> searchUsers(String keyword, int page, int size);

    User getUserById(Integer userId);

    void updateUserRole(Integer userId, Integer roleId);

    void deleteUserById(Integer userId);

    void saveUser(User user);
}