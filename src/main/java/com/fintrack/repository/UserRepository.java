package com.fintrack.repository;

import com.fintrack.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(
            String username,
            Long id);

    // =========================
    // SEARCH
    // =========================

    List<User> findByUsernameContainingIgnoreCase(
            String username);

    // =========================
    // FILTER ROLE
    // =========================

    List<User> findByRoleIgnoreCase(
            String role);

    // =========================
    // FILTER STATUS
    // =========================

    List<User> findByStatusIgnoreCase(
            String status);

    // =========================
    // SEARCH + ROLE
    // =========================

    List<User> findByUsernameContainingIgnoreCaseAndRoleIgnoreCase(
            String username,
            String role);

    // =========================
    // SEARCH + STATUS
    // =========================

    List<User> findByUsernameContainingIgnoreCaseAndStatusIgnoreCase(
            String username,
            String status);

    // =========================
    // ROLE + STATUS
    // =========================

    List<User> findByRoleIgnoreCaseAndStatusIgnoreCase(
            String role,
            String status);

    // =========================
    // SEARCH + ROLE + STATUS
    // =========================

    List<User> findByUsernameContainingIgnoreCaseAndRoleIgnoreCaseAndStatusIgnoreCase(
            String username,
            String role,
            String status);
}