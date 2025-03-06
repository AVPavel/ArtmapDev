package com.example.demo.Security;


import com.example.demo.DBModels.User;
import com.example.demo.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if the default user already exists
            if (userRepository.findByUsername("pavel").isEmpty()) {
                User defaultUser = new User();
                defaultUser.setUsername("pavel");
                defaultUser.setPassword(passwordEncoder.encode("pavel")); // Encode the password
                defaultUser.setEmail("pavel@mail.com");
                defaultUser.setPreferredBudget(new BigDecimal("2.00"));
                defaultUser.setRole(User.Role.USER);
                defaultUser.setCreatedAt(LocalDateTime.now());
                defaultUser.setUpdatedAt(LocalDateTime.now());

                userRepository.save(defaultUser);
                System.out.println("Default user 'pavel' created.");
            }
            else {
                System.out.println("User 'pavel' already exists.");
            }
        };
    }
}
