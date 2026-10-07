package com.fintrack.repository;

import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
        
        
        

    // =========================================================
    // GET ALL
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findAllByOrderByTransactionDateDescIdDesc();

    // =========================================================
    // GET BY USER
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByCreatedByOrderByTransactionDateDescIdDesc(
            String createdBy
    );

    // =========================================================
    // GET BY TYPE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByTypeOrderByTransactionDateDescIdDesc(
            TransactionType type
    );

    // =========================================================
    // GET BY USER + TYPE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByCreatedByAndTypeOrderByTransactionDateDescIdDesc(
            String createdBy,
            TransactionType type
    );

    // =========================================================
    // GET BY DATE RANGE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // GET BY USER + DATE RANGE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByCreatedByAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            String createdBy,
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // GET BY DATE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByTransactionDateOrderByIdDesc(
            LocalDate transactionDate
    );

    // =========================================================
    // GET BY USER + DATE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByCreatedByAndTransactionDateOrderByIdDesc(
            String createdBy,
            LocalDate transactionDate
    );
    
    // =========================================================
        // GET BY ID + USER
        // =========================================================
        
        @EntityGraph(attributePaths = {"category", "fundSource"})
        Optional<Transaction> findByIdAndCreatedBy(
                Long id,
                String createdBy
        );

    // =========================================================
    // RECENT 5
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findTop5ByOrderByTransactionDateDescIdDesc();

    // =========================================================
    // RECENT 5 BY USER
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findTop5ByCreatedByOrderByTransactionDateDescIdDesc(
            String createdBy
    );

    // =========================================================
    // GET BY DATE RANGE - ID DESC
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByTransactionDateBetweenOrderByIdDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // GET BY USER + DATE RANGE - ID DESC
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource"})
    List<Transaction> findByCreatedByAndTransactionDateBetweenOrderByIdDesc(
            String createdBy,
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // TRANSACTION CODE
    // =========================================================

    boolean existsByTransactionCode(String transactionCode);

    // =========================================================
    // DELETE PROTECTION
    // =========================================================

    boolean existsByCategoryId(Long categoryId);

    boolean existsByFundSourceId(Long fundSourceId);

    // =========================================================
    // TOTAL BY TYPE - ALL USER
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.type = :type
    """)
    BigDecimal getTotalByType(
            TransactionType type
    );

    // =========================================================
    // TOTAL BY USER + TYPE
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.createdBy = :createdBy
        AND t.type = :type
    """)
    BigDecimal getTotalByUserAndType(
            String createdBy,
            TransactionType type
    );

    // =========================================================
    // TOTAL BY DATE + TYPE
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.transactionDate = :date
        AND t.type = :type
    """)
    BigDecimal getTotalByDateAndType(
            LocalDate date,
            TransactionType type
    );

    // =========================================================
    // TOTAL BY USER + DATE + TYPE
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.createdBy = :createdBy
        AND t.transactionDate = :date
        AND t.type = :type
    """)
    BigDecimal getTotalByUserAndDateAndType(
            String createdBy,
            LocalDate date,
            TransactionType type
    );

    // =========================================================
    // TOTAL BY DATE RANGE + TYPE
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.transactionDate BETWEEN :startDate AND :endDate
        AND t.type = :type
    """)
    BigDecimal getTotalByDateRangeAndType(
            LocalDate startDate,
            LocalDate endDate,
            TransactionType type
    );

    // =========================================================
    // TOTAL BY USER + DATE RANGE + TYPE
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.createdBy = :createdBy
        AND t.transactionDate BETWEEN :startDate AND :endDate
        AND t.type = :type
    """)
    BigDecimal getTotalByUserAndDateRangeAndType(
            String createdBy,
            LocalDate startDate,
            LocalDate endDate,
            TransactionType type
    );

    // =========================================================
    // COUNT BY DATE RANGE
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.transactionDate BETWEEN :startDate AND :endDate
    """)
    long countByDateRange(
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // COUNT BY USER + DATE RANGE
    // =========================================================

    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.createdBy = :createdBy
        AND t.transactionDate BETWEEN :startDate AND :endDate
    """)
    long countByUserAndDateRange(
            String createdBy,
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // DAILY SUMMARY - ALL USER
    // =========================================================

    @Query("""
        SELECT t.transactionDate,
               t.type,
               COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.transactionDate BETWEEN :startDate AND :endDate
        GROUP BY t.transactionDate, t.type
        ORDER BY t.transactionDate ASC
    """)
    List<Object[]> getDailySummary(
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // DAILY SUMMARY - USER
    // =========================================================

    @Query("""
        SELECT t.transactionDate,
               t.type,
               COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.createdBy = :createdBy
        AND t.transactionDate BETWEEN :startDate AND :endDate
        GROUP BY t.transactionDate, t.type
        ORDER BY t.transactionDate ASC
    """)
    List<Object[]> getDailySummaryByUser(
            String createdBy,
            LocalDate startDate,
            LocalDate endDate
    );
    
    
}