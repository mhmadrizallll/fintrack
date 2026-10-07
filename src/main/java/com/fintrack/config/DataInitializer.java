package com.fintrack.config;

import com.fintrack.entity.User;
import com.fintrack.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();

                admin.setUsername("admin");

                admin.setPassword(
                    passwordEncoder.encode("admin123")
                );

                admin.setFullName("Administrator");

                admin.setEmail("admin@fintrack.local");

                admin.setRole("ADMIN");

                admin.setStatus("ACTIVE");

                userRepository.save(admin);

                System.out.println(
                    "======================================"
                );

                System.out.println(
                    "FINTRACK ADMIN BERHASIL DIBUAT"
                );

                System.out.println(
                    "Username : admin"
                );

                System.out.println(
                    "Password : admin123"
                );

                System.out.println(
                    "======================================"
                );

            } else {

                System.out.println(
                    "Admin sudah tersedia."
                );
            }
        };
    }
}