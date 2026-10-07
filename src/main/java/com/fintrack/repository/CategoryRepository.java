package com.fintrack.repository;

import com.fintrack.entity.Category;
import com.fintrack.entity.CategoryType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    List<Category> findByStatusOrderByNameAsc(
            String status
    );

    List<Category> findByTypeAndStatusOrderByNameAsc(
            CategoryType type,
            String status
    );

    boolean existsByNameIgnoreCase(String name);
}