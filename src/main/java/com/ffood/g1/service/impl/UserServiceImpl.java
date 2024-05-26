package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Role;
import com.ffood.g1.entity.User;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User loadUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public boolean isCodeNameExist(String codeName) {
        return userRepository.findByCodeName(codeName) != null;
    }

    @Override
    public void updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserId()).orElse(null);
        if (existingUser != null) {
            existingUser.setFullName(user.getFullName());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            userRepository.save(existingUser);
        }
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Transactional
    public void registerNewUser(User user) {
        user.setFullName(user.getUserName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());

        Role role = roleRepository.findByName("CUSTOMER");
        if (role == null) {
            throw new IllegalArgumentException("Invalid role");
        } else {
            user.setRole(role);
        }
        userRepository.save(user);
    }

    @Override
    public void saveUserWithDefaultRole(User user) {
        // Implement save user logic if needed
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName())));
    }

    public boolean checkEmailAndPassword(String email, String rawPassword) {
        User user = findByEmail(email);
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}



//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new UsernameNotFoundException("User Invalid");
//        }
//        //check enable or disable validate
//
//        return user; //xác định role thi` có thể xác định các trường account đó checking for staff only
//    }
//
//
//
//
//    @Override
//    public User loadUserById(Integer userId) {
//        return userRepository.findById(userId).orElse(null);
//    }
//
//    @Override
//    public void updateUser(User user) {
//        User existingUser = userRepository.findById(user.getUserId()).orElse(null);
//        if (existingUser != null) {
//            existingUser.setFirstName(user.getFirstName());
//            existingUser.setLastName(user.getLastName());
//            existingUser.setEmail(user.getEmail());
//            existingUser.setPhone(user.getPhone());
//            userRepository.save(existingUser);
//        }
//    }
//
//    @Override
//    public void save(User user) {
//        userRepository.save(user);
//    }
//
//    @Override
//    public void changePassword(Integer userId, String newPassword) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//    }

