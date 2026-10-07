package com.fintrack.controller;

import com.fintrack.entity.User;
import com.fintrack.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // USER LIST
    // =========================

   @GetMapping
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            Model model) {
    
        model.addAttribute(
                "users",
                userService.search(
                        keyword,
                        role,
                        status));
    
        model.addAttribute(
                "keyword",
                keyword != null ? keyword : "");
    
        model.addAttribute(
                "selectedRole",
                role != null ? role : "ALL");
    
        model.addAttribute(
                "selectedStatus",
                status != null ? status : "ALL");
    
        model.addAttribute(
                "pageTitle",
                "User Management");
    
        model.addAttribute(
                "pageSubtitle",
                "Kelola pengguna FinTrack");
    
        model.addAttribute(
                "activeMenu",
                "users");
    
        return "admin/users";
    }
    // =========================
    // CREATE FORM
    // =========================

    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute(
                "pageTitle",
                "Tambah User");

        model.addAttribute(
                "pageSubtitle",
                "Tambah pengguna baru");

        model.addAttribute(
                "activeMenu",
                "users");

        return "admin/user-form";
    }

    // =========================
    // CREATE
    // =========================

    @PostMapping
    public String create(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam String role,
            @RequestParam String status,
            Model model) {

        try {

            userService.create(
                    username,
                    password,
                    fullName,
                    email,
                    role,
                    status);

            return "redirect:/admin/users?success=created";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage());

            model.addAttribute(
                    "username",
                    username);

            model.addAttribute(
                    "fullName",
                    fullName);

            model.addAttribute(
                    "email",
                    email);

            model.addAttribute(
                    "role",
                    role);

            model.addAttribute(
                    "status",
                    status);

            model.addAttribute(
                    "pageTitle",
                    "Tambah User");

            model.addAttribute(
                    "pageSubtitle",
                    "Tambah pengguna baru");

            model.addAttribute(
                    "activeMenu",
                    "users");

            return "admin/user-form";
        }
    }

    // =========================
    // EDIT FORM
    // =========================

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "user",
                userService.findById(id));

        model.addAttribute(
                "pageTitle",
                "Edit User");

        model.addAttribute(
                "pageSubtitle",
                "Ubah informasi pengguna");

        model.addAttribute(
                "activeMenu",
                "users");

        return "admin/user-form-edit";
    }

    // =========================
    // UPDATE
    // =========================

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam String role,
            @RequestParam String status,
            Model model,
            Authentication authentication) {

        try {

            User existingUser = userService.findById(id);

            String currentUsername =
                    authentication.getName();

            // Jangan izinkan user mengganti role dirinya
            // dari ADMIN menjadi USER.
            if (existingUser.getUsername()
                    .equalsIgnoreCase(currentUsername)) {

                if (!"ADMIN".equalsIgnoreCase(role)) {

                    throw new IllegalArgumentException(
                            "Anda tidak dapat mengubah role akun sendiri");
                }

                // Jangan izinkan admin menonaktifkan dirinya
                if (!"ACTIVE".equalsIgnoreCase(status)) {

                    throw new IllegalArgumentException(
                            "Anda tidak dapat menonaktifkan akun sendiri");
                }
            }

            userService.update(
                    id,
                    username,
                    password,
                    fullName,
                    email,
                    role,
                    status);

            return "redirect:/admin/users?success=updated";

        } catch (IllegalArgumentException e) {

            User user = new User();

            user.setId(id);
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setRole(role);
            user.setStatus(status);

            model.addAttribute(
                    "user",
                    user);

            model.addAttribute(
                    "error",
                    e.getMessage());

            model.addAttribute(
                    "pageTitle",
                    "Edit User");

            model.addAttribute(
                    "pageSubtitle",
                    "Ubah informasi pengguna");

            model.addAttribute(
                    "activeMenu",
                    "users");

            return "admin/user-form-edit";
        }
    }

    // =========================
    // TOGGLE STATUS
    // =========================

    @PostMapping("/toggle/{id}")
    public String toggleStatus(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            userService.toggleStatus(
                    id,
                    authentication.getName());

            return "redirect:/admin/users?success=status";

        } catch (IllegalArgumentException e) {

            return "redirect:/admin/users?error="
                    + e.getMessage();
        }
    }
}