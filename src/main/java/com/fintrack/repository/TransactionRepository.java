
package com.fintrack.repository;

import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    // =========================================================
    // GET ALL
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findAllByOrderByTransactionDateDescIdDesc();

    // =========================================================
    // PAGINATION - GET ALL
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    Page<Transaction> findAllByOrderByTransactionDateDescIdDesc(
            Pageable pageable
    );

    // =========================================================
    // GET BY USER
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByCreatedByOrderByTransactionDateDescIdDesc(
            String createdBy
    );

    // =========================================================
    // PAGINATION - GET BY USER
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    Page<Transaction> findByCreatedByOrderByTransactionDateDescIdDesc(
            String createdBy,
            Pageable pageable
    );

    // =========================================================
    // GET BY TYPE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByTypeOrderByTransactionDateDescIdDesc(
            TransactionType type
    );

    // =========================================================
    // GET BY USER + TYPE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByCreatedByAndTypeOrderByTransactionDateDescIdDesc(
            String createdBy,
            TransactionType type
    );

    // =========================================================
    // GET BY DATE RANGE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // GET BY USER + DATE RANGE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByCreatedByAndTransactionDateBetweenOrderByTransactionDateDescIdDesc(
            String createdBy,
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // GET BY DATE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByTransactionDateOrderByIdDesc(
            LocalDate transactionDate
    );

    // =========================================================
    // GET BY USER + DATE
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByCreatedByAndTransactionDateOrderByIdDesc(
            String createdBy,
            LocalDate transactionDate
    );

    // =========================================================
    // GET BY ID + USER
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    Optional<Transaction> findByIdAndCreatedBy(
            Long id,
            String createdBy
    );

    // =========================================================
    // RECENT 5
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findTop5ByOrderByTransactionDateDescIdDesc();

    // =========================================================
    // RECENT 5 BY USER
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findTop5ByCreatedByOrderByTransactionDateDescIdDesc(
            String createdBy
    );

    // =========================================================
    // GET BY DATE RANGE - ID DESC
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
    List<Transaction> findByTransactionDateBetweenOrderByIdDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    // =========================================================
    // GET BY USER + DATE RANGE - ID DESC
    // =========================================================

    @EntityGraph(attributePaths = {"category", "fundSource", "destinationFundSource"})
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

    boolean existsByDestinationFundSourceId(Long fundSourceId);

    // =========================================================
    // TOTAL BY TYPE - ALL USER
    // =========================================================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.type = :type
    """)
    BigDecimal getTotalByType(
            @Param("type") TransactionType type
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
            @Param("createdBy") String createdBy,
            @Param("type") TransactionType type
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
            @Param("date") LocalDate date,
            @Param("type") TransactionType type
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
            @Param("createdBy") String createdBy,
            @Param("date") LocalDate date,
            @Param("type") TransactionType type
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
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type
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
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type
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
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
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
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
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
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
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
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // =========================================================
    // BALANCE BY FUND SOURCE
    //
    // INCOME   = positive
    // EXPENSE  = negative
    // TRANSFER = handled as outgoing/incoming separately
    // =========================================================

    @Query("""
        SELECT
            COALESCE(SUM(
                CASE
                    WHEN t.type = :income
         AND t.fundSource.id = :fundSourceId
         THEN t.amount

    WHEN t.type = :expense
         AND t.fundSource.id = :fundSourceId
         THEN -t.amount

    WHEN t.type = :transfer
         AND t.fundSource.id = :fundSourceId
         THEN -t.amount

    WHEN t.type = :transfer
         AND t.destinationFundSource.id = :fundSourceId
         THEN t.amount

    ELSE 0
                END
            ), 0)
        FROM Transaction t
        WHERE t.fundSource.id = :fundSourceId
           OR t.destinationFundSource.id = :fundSourceId
    """)
    BigDecimal calculateBalanceByFundSource(
            @Param("fundSourceId") Long fundSourceId,
            @Param("income") TransactionType income,
            @Param("expense") TransactionType expense,
            @Param("transfer") TransactionType transfer
    );

    // =========================================================
    // BALANCE BY FUND SOURCE + USER
    // =========================================================

    @Query("""
        SELECT
            COALESCE(SUM(
                CASE
                    WHEN t.type = :income
         AND t.fundSource.id = :fundSourceId
         THEN t.amount

    WHEN t.type = :expense
         AND t.fundSource.id = :fundSourceId
         THEN -t.amount

    WHEN t.type = :transfer
         AND t.fundSource.id = :fundSourceId
         THEN -t.amount

    WHEN t.type = :transfer
         AND t.destinationFundSource.id = :fundSourceId
         THEN t.amount

    ELSE 0
                END
            ), 0)
        FROM Transaction t
        WHERE (t.fundSource.id = :fundSourceId
            OR t.destinationFundSource.id = :fundSourceId)
          AND t.createdBy = :createdBy
    """)
    BigDecimal calculateBalanceByFundSourceAndUser(
            @Param("fundSourceId") Long fundSourceId,
            @Param("createdBy") String createdBy,
            @Param("income") TransactionType income,
            @Param("expense") TransactionType expense,
            @Param("transfer") TransactionType transfer
    );

    // =========================================================
    // ATTACHMENT BY USER
    // =========================================================

    Optional<Transaction> findByAttachmentAndCreatedBy(
            String attachment,
            String createdBy
    );
}
