package com.ffood.g1;

import com.ffood.g1.service.impl.UserServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@SpringBootApplication
public class G1Application {

	public static void main(String[] args) {

//				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//				String rawPassword = "123456";
//				String encodedPassword = encoder.encode(rawPassword);
//				System.out.println(encodedPassword);

		SpringApplication.run(G1Application.class, args);


	}

}
