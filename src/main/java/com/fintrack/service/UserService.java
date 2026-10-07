package com.fintrack.service;

import com.fintrack.entity.User;
import com.fintrack.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // FIND ALL
    // =========================

    public List<User> findAll() {
        return userRepository.findAll();
    }

    // =========================
    // FIND BY ID
    // =========================

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User tidak ditemukan"));
    }

    // =========================
    // CREATE
    // =========================

    public void create(
            String username,
            String password,
            String fullName,
            String email,
            String role,
            String status) {

        // Username
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username wajib diisi");
        }

        username = username.trim();

        // Password
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password wajib diisi");
        }

        // Username duplicate
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username sudah digunakan");
        }

        // Validate role
        validateRole(role);

        // Validate status
        validateStatus(status);

        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        user.setFullName(
                fullName != null && !fullName.isBlank()
                        ? fullName.trim()
                        : null);

        user.setEmail(
                email != null && !email.isBlank()
                        ? email.trim()
                        : null);

        user.setRole(role);
        user.setStatus(status);

        userRepository.save(user);
    }

    // =========================
    // UPDATE
    // =========================

    public void update(
            Long id,
            String username,
            String password,
            String fullName,
            String email,
            String role,
            String status) {

        User user = findById(id);

        // Username
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username wajib diisi");
        }

        username = username.trim();

        // Duplicate username
        if (userRepository.existsByUsernameAndIdNot(username, id)) {
            throw new IllegalArgumentException(
                    "Username sudah digunakan");
        }

        // Validate role
        validateRole(role);

        // Validate status
        validateStatus(status);

        // Update username
        user.setUsername(username);

        // Update full name
        user.setFullName(
                fullName != null && !fullName.isBlank()
                        ? fullName.trim()
                        : null);

        // Update email
        user.setEmail(
                email != null && !email.isBlank()
                        ? email.trim()
                        : null);

        // Update role
        user.setRole(role);

        // Update status
        user.setStatus(status);

        // Password hanya diubah jika diisi
        if (password != null && !password.isBlank()) {
            user.setPassword(
                    passwordEncoder.encode(password));
        }

        userRepository.save(user);
    }

    // =========================
    // TOGGLE STATUS
    // =========================

    public void toggleStatus(Long id, String currentUsername) {

        User user = findById(id);

        // Jangan izinkan admin menonaktifkan dirinya sendiri
        if (user.getUsername().equalsIgnoreCase(currentUsername)) {

            throw new IllegalArgumentException(
                    "Anda tidak dapat menonaktifkan akun sendiri");
        }

        if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {

            // Kalau yang akan dinonaktifkan adalah ADMIN,
            // pastikan masih ada ADMIN aktif lainnya.
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {

                long activeAdminCount = userRepository.findAll()
                        .stream()
                        .filter(u ->
                                "ADMIN".equalsIgnoreCase(u.getRole())
                                        && "ACTIVE".equalsIgnoreCase(u.getStatus()))
                        .count();

                if (activeAdminCount <= 1) {
                    throw new IllegalArgumentException(
                            "Admin aktif terakhir tidak dapat dinonaktifkan");
                }
            }

            user.setStatus("INACTIVE");

        } else {

            user.setStatus("ACTIVE");
        }

        userRepository.save(user);
    }

    // =========================
    // VALIDATION ROLE
    // =========================

    private void validateRole(String role) {

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException(
                    "Role wajib dipilih");
        }

        if (!"ADMIN".equalsIgnoreCase(role)
                && !"USER".equalsIgnoreCase(role)) {

            throw new IllegalArgumentException(
                    "Role tidak valid");
        }
    }

    // =========================
    // VALIDATION STATUS
    // =========================

    private void validateStatus(String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Status wajib dipilih");
        }

        if (!"ACTIVE".equalsIgnoreCase(status)
                && !"INACTIVE".equalsIgnoreCase(status)) {

            throw new IllegalArgumentException(
                    "Status tidak valid");
        }
    }
    
    public List<User> search(
            String keyword,
            String role,
            String status) {
    
        boolean hasKeyword =
                keyword != null && !keyword.isBlank();
    
        boolean hasRole =
                role != null
                        && !role.isBlank()
                        && !"ALL".equalsIgnoreCase(role);
    
        boolean hasStatus =
                status != null
                        && !status.isBlank()
                        && !"ALL".equalsIgnoreCase(status);
    
        // =========================
        // SEARCH + ROLE + STATUS
        // =========================
    
        if (hasKeyword && hasRole && hasStatus) {
    
            return userRepository
                    .findByUsernameContainingIgnoreCaseAndRoleIgnoreCaseAndStatusIgnoreCase(
                            keyword.trim(),
                            role,
                            status);
        }
    
        // =========================
        // SEARCH + ROLE
        // =========================
    
        if (hasKeyword && hasRole) {
    
            return userRepository
                    .findByUsernameContainingIgnoreCaseAndRoleIgnoreCase(
                            keyword.trim(),
                            role);
        }
    
        // =========================
        // SEARCH + STATUS
        // =========================
    
        if (hasKeyword && hasStatus) {
    
            return userRepository
                    .findByUsernameContainingIgnoreCaseAndStatusIgnoreCase(
                            keyword.trim(),
                            status);
        }
    
        // =========================
        // ROLE + STATUS
        // =========================
    
        if (hasRole && hasStatus) {
    
            return userRepository
                    .findByRoleIgnoreCaseAndStatusIgnoreCase(
                            role,
                            status);
        }
    
        // =========================
        // SEARCH ONLY
        // =========================
    
        if (hasKeyword) {
    
            return userRepository
                    .findByUsernameContainingIgnoreCase(
                            keyword.trim());
        }
    
        // =========================
        // ROLE ONLY
        // =========================
    
        if (hasRole) {
    
            return userRepository
                    .findByRoleIgnoreCase(role);
        }
    
        // =========================
        // STATUS ONLY
        // =========================
    
        if (hasStatus) {
    
            return userRepository
                    .findByStatusIgnoreCase(status);
        }
    
        // =========================
        // ALL
        // =========================
    
        return userRepository.findAll();
    }
}