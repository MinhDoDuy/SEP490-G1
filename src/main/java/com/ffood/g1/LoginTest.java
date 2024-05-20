package com.ffood.g1;

import com.ffood.g1.entity.User;
import com.ffood.g1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

//@SpringBootApplication
public class LoginTest {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(LoginTest.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            // Test the login functionality
            String email = "chuvuz04112001@gmail.com";
            String password = "123";

            User user = userService.login(email, password);
            if (user != null) {
                System.out.println("Login successful! User: " + user.getEmail());
            } else {
                System.out.println("Login failed for email: " + email);
            }
        };
    }
}

