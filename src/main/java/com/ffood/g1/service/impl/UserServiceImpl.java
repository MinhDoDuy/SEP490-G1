package com.ffood.g1.service.impl;

import com.ffood.g1.entity.User;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }
        return user;
    }

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("User not found for email: " + email);
        } else {
            System.out.println("User found: " + user.getEmail());
            if (passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("Password matches");
                return user;
            } else {
                System.out.println("Password does not match");
            }
        }
        return null;
    }

    @Override
    public User getCurrentUser() {
        int userId = 1; // Cố định userId = 1
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void updateUser(User user) {
        User existingUser = getCurrentUser();
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        userRepository.save(existingUser);
    }
}
