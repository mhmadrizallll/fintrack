
package com.fintrack.controller;

import com.fintrack.entity.Category;
import com.fintrack.entity.CategoryType;
import com.fintrack.service.CategoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    @GetMapping
    public String index(Model model) {
    
        model.addAttribute(
                "pageTitle",
                "Categories"
        );
    
        model.addAttribute(
                "pageSubtitle",
                "Kelola kategori pemasukan dan pengeluaran"
        );
    
        model.addAttribute(
                "activeMenu",
                "categories"
        );
    
        model.addAttribute(
                "categories",
                categoryService.getAllActive()
        );
    
        return "categories/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
    
        model.addAttribute(
                "pageTitle",
                "Tambah Category"
        );
    
        model.addAttribute(
                "pageSubtitle",
                "Buat kategori pemasukan atau pengeluaran"
        );
    
        model.addAttribute(
                "activeMenu",
                "categories"
        );
    
        model.addAttribute(
                "category",
                new Category()
        );
    
        model.addAttribute(
                "categoryTypes",
                CategoryType.values()
        );
    
        return "categories/form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute("category") Category category,
            RedirectAttributes redirectAttributes) {

        try {

            boolean isNew = category.getId() == null;

            categoryService.save(category);

            redirectAttributes.addFlashAttribute(
                    "success",
                    isNew
                            ? "Category berhasil ditambahkan."
                            : "Category berhasil diperbarui."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Gagal menyimpan category: " + e.getMessage()
            );
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {
    
        model.addAttribute(
                "pageTitle",
                "Edit Category"
        );
    
        model.addAttribute(
                "pageSubtitle",
                "Perbarui informasi kategori"
        );
    
        model.addAttribute(
                "activeMenu",
                "categories"
        );
    
        model.addAttribute(
                "category",
                categoryService.getById(id)
        );
    
        model.addAttribute(
                "categoryTypes",
                CategoryType.values()
        );
    
        return "categories/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            categoryService.deactivate(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Category berhasil dinonaktifkan."
            );

        } catch (IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Gagal menonaktifkan category: "
                            + e.getMessage()
            );
        }

        return "redirect:/admin/categories";
    }
}
