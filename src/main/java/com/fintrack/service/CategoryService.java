package com.fintrack.service;

import com.fintrack.entity.Category;
import com.fintrack.entity.CategoryType;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository) {

        this.categoryRepository =
                categoryRepository;

        this.transactionRepository =
                transactionRepository;
    }

    // =========================================================
    // GET ALL ACTIVE
    // =========================================================

    public List<Category> getAllActive() {

        return categoryRepository
                .findByStatusOrderByNameAsc("ACTIVE");
    }

    // =========================================================
    // GET BY TYPE
    // =========================================================

    public List<Category> getByType(
            CategoryType type) {

        return categoryRepository
                .findByTypeAndStatusOrderByNameAsc(
                        type,
                        "ACTIVE"
                );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    public Category getById(Long id) {

        return categoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category tidak ditemukan: "
                                        + id
                        )
                );
    }

    // =========================================================
    // SAVE
    // CREATE / UPDATE
    // =========================================================

    public Category save(
            Category category) {

        // =====================================================
        // CREATE
        // =====================================================

        if (category.getId() == null) {

            if (category.getStatus() == null
                    || category.getStatus().isBlank()) {

                category.setStatus("ACTIVE");
            }

            return categoryRepository.save(
                    category
            );
        }

        // =====================================================
        // UPDATE
        // =====================================================

        Category existing =
                categoryRepository
                        .findById(
                                category.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Category tidak ditemukan: "
                                                + category.getId()
                                )
                        );

        existing.setName(
                category.getName()
        );

        existing.setType(
                category.getType()
        );

        existing.setDescription(
                category.getDescription()
        );

        if (category.getStatus() != null
                && !category.getStatus().isBlank()) {

            existing.setStatus(
                    category.getStatus()
            );
        }

        return categoryRepository.save(
                existing
        );
    }

    // =========================================================
    // DELETE / DEACTIVATE
    // =========================================================

    public void deactivate(Long id) {

        // -----------------------------------------------------
        // Pastikan category ada
        // -----------------------------------------------------

        Category category =
                getById(id);

        // -----------------------------------------------------
        // Cek apakah sudah digunakan transaksi
        // -----------------------------------------------------

        boolean used =
                transactionRepository
                        .existsByCategoryId(id);

        if (used) {

            throw new IllegalStateException(
                    "Category \""
                            + category.getName()
                            + "\" tidak dapat dihapus "
                            + "karena sudah digunakan "
                            + "pada transaksi."
            );
        }

        // -----------------------------------------------------
        // Kalau belum digunakan → INACTIVE
        // -----------------------------------------------------

        category.setStatus("INACTIVE");

        categoryRepository.save(
                category
        );
    }
}