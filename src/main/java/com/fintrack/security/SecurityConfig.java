package com.fintrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // =====================================================
                // PUBLIC
                // =====================================================

                .requestMatchers(
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                ).permitAll()


                // =====================================================
                // USER + ADMIN
                // =====================================================

                .requestMatchers(
                        "/dashboard"
                ).authenticated()

                .requestMatchers(
                        "/transactions/**"
                ).authenticated()

                .requestMatchers(
                        "/reports/**"
                ).authenticated()


                // =====================================================
                // ADMIN ONLY
                // =====================================================

                .requestMatchers(
                        "/admin/**"
                ).hasRole("ADMIN")


                // =====================================================
                // DEFAULT
                // =====================================================

                .anyRequest().authenticated()
            )


            // =========================================================
            // LOGIN
            // =========================================================

            .formLogin(form -> form

                .loginPage("/login")

                .defaultSuccessUrl(
                        "/dashboard",
                        true
                )

                .permitAll()
            )


            // =========================================================
            // LOGOUT
            // =========================================================

            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl(
                        "/login?logout"
                )

                .permitAll()
            );

        return http.build();
    }
}