package com.ffood.g1.service.impl;

import com.ffood.g1.entity.User;
import com.ffood.g1.repository.UserRepository;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new UsernameNotFoundException("User not found");
        }
        // Check enable or disable validate

        return user; // Xác định role có thể xác định các trường account đó checking for staff only
    }

    @Override
    public User getCurrentUser() {
        int userId = 1; // Cố định userId = 1
        return userRepository.findById((long) userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void updateUser(User user) {
        User existingUser = getCurrentUser();
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        userRepository.save(existingUser);
    }

    @Override
    public User login(String email, String password) {
        return authenticate(email, password);
    }

    @Override
    public boolean isEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public User register(User user) {
        // Encode password before saving to database
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return user;
    }
}
