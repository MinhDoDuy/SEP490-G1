package com.ffood.g1.service;

import com.ffood.g1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User loadUserById(Integer userId);

    boolean isPhoneExist(String phone);

    //edit profile user
    void updateUser(User user);

    boolean isEmailExist(String email);

    void registerNewUser(User user);


    boolean isCodeNameExist(String codeName);

    //forgot pass
    void sendResetPasswordEmail(String email, HttpServletRequest request);
    boolean isResetTokenValid(String token);
    void updatePasswordReset(String token, String password);

    //change pass
    void updatePassword(User user, String newPassword);

    //List người dùng cho admin đã có phân trang
    Page<User>  getAllUsers(int page, int size);

    //search user
    Page<User> searchUsersFilter(String keyword, Integer roleId, Integer canteenId, int page, int size);


    User getUserById(Integer userId);

    void updateUserStatus(Integer userId, Integer roleId , Boolean isActive , Integer canteenId);
    
    void saveUser(User user);


    Integer countUsers();

    Page<User> getAllStaff(int page, int size, Integer canteenId);

    Page<User> getStaffUsers(int page, int size);

    Page<User> searchStaff(String keyword, int page, int size);

}