package com.fintrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "USERNAME",
        nullable = false,
        unique = true,
        length = 50
    )
    private String username;

    @Column(
        name = "PASSWORD",
        nullable = false,
        length = 255
    )
    private String password;

    @Column(
        name = "FULL_NAME",
        nullable = false,
        length = 100
    )
    private String fullName;

    @Column(
        name = "EMAIL",
        length = 150
    )
    private String email;

    @Column(
        name = "ROLE",
        nullable = false,
        length = 20
    )
    private String role = "USER";

    @Column(
        name = "STATUS",
        nullable = false,
        length = 20
    )
    private String status = "ACTIVE";

    @Column(
        name = "CREATED_AT",
        nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
        name = "UPDATED_AT"
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // =========================
    // GETTER & SETTER
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}