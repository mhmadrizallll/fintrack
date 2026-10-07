package com.fintrack.repository;

import com.fintrack.entity.FundSource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundSourceRepository
        extends JpaRepository<FundSource, Long> {

    List<FundSource> findByStatusOrderByNameAsc(
            String status
    );

    boolean existsByNameIgnoreCase(String name);
}