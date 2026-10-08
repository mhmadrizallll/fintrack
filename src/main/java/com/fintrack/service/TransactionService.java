package com.fintrack.service;

import com.fintrack.entity.Category;
import com.fintrack.entity.FundSource;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.FundSourceRepository;
import com.fintrack.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final FundSourceRepository fundSourceRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            FundSourceRepository fundSourceRepository) {

        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.fundSourceRepository = fundSourceRepository;
    }

    // =========================================================
    // GET ALL
    // =========================================================

    public List<Transaction> getAll() {
        return transactionRepository
                .findAllByOrderByTransactionDateDescIdDesc();
    }
    
    // =========================================================
        // GET ALL BY USER
        // =========================================================
        
        public List<Transaction> getAllByUser(
                String username) {
        
            return transactionRepository
                    .findByCreatedByOrderByTransactionDateDescIdDesc(
                            username
                    );
        }
        
        // =========================================================
// PAGINATION - GET ALL
// =========================================================

public Page<Transaction> getAllPaginated(
        int page,
        int size) {

    Pageable pageable =
            PageRequest.of(
                    page,
                    size
            );

    return transactionRepository
            .findAllByOrderByTransactionDateDescIdDesc(
                    pageable
            );
}

// =========================================================
// PAGINATION - GET ALL BY USER
// =========================================================

public Page<Transaction> getAllByUserPaginated(
        String username,
        int page,
        int size) {

    Pageable pageable =
            PageRequest.of(
                    page,
                    size
            );

    return transactionRepository
            .findByCreatedByOrderByTransactionDateDescIdDesc(
                    username,
                    pageable
            );
}
        
        // =========================================================
        // GET BY USER + DATE
        // =========================================================
        
        public List<Transaction> getTransactionsByDate(
                LocalDate date,
                String username) {
        
            return transactionRepository
                    .findByCreatedByAndTransactionDateOrderByIdDesc(
                            username,
                            date
                    );
        }
        
        // =========================================================
        // GET BY USER + DATE RANGE
        // =========================================================
        
        public List<Transaction> getTransactionsByDateRange(
                LocalDate startDate,
                LocalDate endDate,
                String username) {
        
            return transactionRepository
                    .findByCreatedByAndTransactionDateBetweenOrderByIdDesc(
                            username,
                            startDate,
                            endDate
                    );
        }

    // =========================================================
    // RECENT TRANSACTIONS
    // =========================================================

    public List<Transaction> getRecentTransactions() {
        return transactionRepository
                .findTop5ByOrderByTransactionDateDescIdDesc();
    }

    // =========================================================
    // GET BY DATE
    // =========================================================

    public List<Transaction> getTransactionsByDate(
            LocalDate date) {

        return transactionRepository
                .findByTransactionDateOrderByIdDesc(date);
    }

    // =========================================================
    // GET BY DATE RANGE
    // =========================================================

    public List<Transaction> getTransactionsByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        return transactionRepository
                .findByTransactionDateBetweenOrderByIdDesc(
                        startDate,
                        endDate
                );
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    public Transaction getById(Long id) {

        return transactionRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction tidak ditemukan: " + id
                        )
                );
    }
    
    // =========================================================
        // GET BY ID + USER
        // =========================================================
        
        public Transaction getByIdAndUser(
                Long id,
                String username) {
        
            return transactionRepository
                    .findByIdAndCreatedBy(id, username)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Transaction tidak ditemukan atau bukan milik Anda"
                            )
                    );
        }

    // =========================================================
    // SAVE / CREATE / UPDATE
    // =========================================================

    public Transaction save(Transaction transaction) {

        // =====================================================
        // CREATE
        // =====================================================

        if (transaction.getId() == null) {

            if (transaction.getTransactionCode() == null
                    || transaction.getTransactionCode().isBlank()) {

                transaction.setTransactionCode(
                        generateTransactionCode()
                );
            }

            if (transaction.getTransactionDate() == null) {

                transaction.setTransactionDate(
                        LocalDate.now()
                );
            }

            return transactionRepository.save(transaction);
        }

        // =====================================================
        // UPDATE
        // =====================================================

        Transaction existingTransaction =
                transactionRepository
                        .findById(transaction.getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Transaction tidak ditemukan: "
                                                + transaction.getId()
                                )
                        );

        // -----------------------------------------------------
        // UPDATE DATE
        // -----------------------------------------------------

        existingTransaction.setTransactionDate(
                transaction.getTransactionDate()
        );

        // -----------------------------------------------------
        // UPDATE TYPE
        // -----------------------------------------------------

        existingTransaction.setType(
                transaction.getType()
        );

        // -----------------------------------------------------
        // UPDATE CATEGORY
        // -----------------------------------------------------

        if (transaction.getCategory() != null
                && transaction.getCategory().getId() != null) {

            Category category =
                    categoryRepository
                            .findById(
                                    transaction
                                            .getCategory()
                                            .getId()
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Category tidak ditemukan"
                                    )
                            );

            existingTransaction.setCategory(
                    category
            );
        }

        // -----------------------------------------------------
        // UPDATE FUND SOURCE
        // -----------------------------------------------------

        if (transaction.getFundSource() != null
                && transaction.getFundSource().getId() != null) {

            FundSource fundSource =
                    fundSourceRepository
                            .findById(
                                    transaction
                                            .getFundSource()
                                            .getId()
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Fund Source tidak ditemukan"
                                    )
                            );

            existingTransaction.setFundSource(
                    fundSource
            );
        }

        // -----------------------------------------------------
        // UPDATE AMOUNT
        // -----------------------------------------------------

        existingTransaction.setAmount(
                transaction.getAmount()
        );

        // -----------------------------------------------------
        // UPDATE DESCRIPTION
        // -----------------------------------------------------

        existingTransaction.setDescription(
                transaction.getDescription()
        );
        

        // -----------------------------------------------------
        // CREATED BY TETAP
        // -----------------------------------------------------
        //
        // createdBy tidak kita ubah ketika edit.
        // Yang membuat transaksi tetap tercatat sebagai
        // user yang pertama kali membuat transaksi tersebut.
        //

        // -----------------------------------------------------
        // TRANSACTION CODE TETAP
        // -----------------------------------------------------
        //
        // Kode transaksi tidak dibuat ulang ketika edit.
        //
        
        existingTransaction.setAttachment(
                transaction.getAttachment()
        );


        return transactionRepository.save(
                existingTransaction
        );
    }
        
        // =========================================================
// UPDATE BY USER
// =========================================================

public Transaction updateByUser(
        Transaction transaction,
        String username) {

    Transaction existingTransaction =
            transactionRepository
                    .findByIdAndCreatedBy(
                            transaction.getId(),
                            username
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Transaction tidak ditemukan atau bukan milik Anda"
                            )
                    );

    // -----------------------------------------------------
    // UPDATE DATE
    // -----------------------------------------------------

    existingTransaction.setTransactionDate(
            transaction.getTransactionDate()
    );

    // -----------------------------------------------------
    // UPDATE TYPE
    // -----------------------------------------------------

    existingTransaction.setType(
            transaction.getType()
    );

    // -----------------------------------------------------
    // UPDATE CATEGORY
    // -----------------------------------------------------

    if (transaction.getCategory() != null
            && transaction.getCategory().getId() != null) {

        Category category =
                categoryRepository
                        .findById(
                                transaction
                                        .getCategory()
                                        .getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Category tidak ditemukan"
                                )
                        );

        existingTransaction.setCategory(category);
    }

    // -----------------------------------------------------
    // UPDATE FUND SOURCE
    // -----------------------------------------------------

    if (transaction.getFundSource() != null
            && transaction.getFundSource().getId() != null) {

        FundSource fundSource =
                fundSourceRepository
                        .findById(
                                transaction
                                        .getFundSource()
                                        .getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Fund Source tidak ditemukan"
                                )
                        );

        existingTransaction.setFundSource(fundSource);
    }

    // -----------------------------------------------------
    // UPDATE AMOUNT
    // -----------------------------------------------------

    existingTransaction.setAmount(
            transaction.getAmount()
    );

    // -----------------------------------------------------
    // UPDATE DESCRIPTION
    // -----------------------------------------------------

    existingTransaction.setDescription(
            transaction.getDescription()
    );

    // -----------------------------------------------------
    // UPDATE ATTACHMENT
    // -----------------------------------------------------

    existingTransaction.setAttachment(
            transaction.getAttachment()
    );

    // -----------------------------------------------------
    // CREATED BY TETAP
    // -----------------------------------------------------

    return transactionRepository.save(
            existingTransaction
    );
}

// =========================================================
// DELETE BY USER
// =========================================================

public void deleteByUser(
        Long id,
        String username) {

    Transaction transaction =
            transactionRepository
                    .findByIdAndCreatedBy(
                            id,
                            username
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Transaction tidak ditemukan atau bukan milik Anda"
                            )
                    );

    transactionRepository.delete(transaction);
}

    // =========================================================
    // DELETE
    // =========================================================

    public void delete(Long id) {

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Transaction tidak ditemukan: "
                                                + id
                                ));
    
        transactionRepository.delete(transaction);
    }

    // =========================================================
    // TOTAL INCOME
    // =========================================================

    public BigDecimal getTotalIncome() {

        return transactionRepository.getTotalByType(
                TransactionType.INCOME
        );
    }

    // =========================================================
    // TOTAL EXPENSE
    // =========================================================

    public BigDecimal getTotalExpense() {

        return transactionRepository.getTotalByType(
                TransactionType.EXPENSE
        );
    }
    
    // =========================================================
// TOTAL INCOME BY USER
// =========================================================

public BigDecimal getTotalIncomeByUser(String username) {

    return transactionRepository.getTotalByUserAndType(
            username,
            TransactionType.INCOME
    );
}

// =========================================================
// TOTAL EXPENSE BY USER
// =========================================================

public BigDecimal getTotalExpenseByUser(String username) {

    return transactionRepository.getTotalByUserAndType(
            username,
            TransactionType.EXPENSE
    );
}

// =========================================================
// BALANCE BY USER
// =========================================================

public BigDecimal getBalanceByUser(String username) {

    return getTotalIncomeByUser(username)
            .subtract(
                    getTotalExpenseByUser(username)
            );
}

// =========================================================
// TODAY INCOME BY USER
// =========================================================

public BigDecimal getTodayIncomeByUser(String username) {

    return transactionRepository.getTotalByUserAndDateAndType(
            username,
            LocalDate.now(),
            TransactionType.INCOME
    );
}

// =========================================================
// TODAY EXPENSE BY USER
// =========================================================

public BigDecimal getTodayExpenseByUser(String username) {

    return transactionRepository.getTotalByUserAndDateAndType(
            username,
            LocalDate.now(),
            TransactionType.EXPENSE
    );
}

// =========================================================
// RECENT TRANSACTIONS BY USER
// =========================================================

public List<Transaction> getRecentTransactionsByUser(String username) {

    return transactionRepository
            .findTop5ByCreatedByOrderByTransactionDateDescIdDesc(
                    username
            );
}

    // =========================================================
    // BALANCE
    // =========================================================

    public BigDecimal getBalance() {

        return getTotalIncome()
                .subtract(
                        getTotalExpense()
                );
    }

    // =========================================================
    // TODAY INCOME
    // =========================================================

    public BigDecimal getTodayIncome() {

        return transactionRepository
                .getTotalByDateAndType(
                        LocalDate.now(),
                        TransactionType.INCOME
                );
    }

    // =========================================================
    // TODAY EXPENSE
    // =========================================================

    public BigDecimal getTodayExpense() {

        return transactionRepository
                .getTotalByDateAndType(
                        LocalDate.now(),
                        TransactionType.EXPENSE
                );
    }

    // =========================================================
    // INCOME BY DATE
    // =========================================================

    public BigDecimal getIncomeByDate(
            LocalDate date) {

        return transactionRepository
                .getTotalByDateAndType(
                        date,
                        TransactionType.INCOME
                );
    }
    
    // =========================================================
        // INCOME BY DATE + USER
        // =========================================================
        
        public BigDecimal getIncomeByDate(
                LocalDate date,
                String username) {
        
            return transactionRepository
                    .getTotalByUserAndDateAndType(
                            username,
                            date,
                            TransactionType.INCOME
                    );
        }

    // =========================================================
    // EXPENSE BY DATE
    // =========================================================

    public BigDecimal getExpenseByDate(
            LocalDate date) {

        return transactionRepository
                .getTotalByDateAndType(
                        date,
                        TransactionType.EXPENSE
                );
    }
    
    // =========================================================
        // EXPENSE BY DATE + USER
        // =========================================================
        
        public BigDecimal getExpenseByDate(
                LocalDate date,
                String username) {
        
            return transactionRepository
                    .getTotalByUserAndDateAndType(
                            username,
                            date,
                            TransactionType.EXPENSE
                    );
        }

    // =========================================================
    // BALANCE BY DATE
    // =========================================================

    public BigDecimal getBalanceByDate(
            LocalDate date) {

        return getIncomeByDate(date)
                .subtract(
                        getExpenseByDate(date)
                );
    }
    
    // =========================================================
        // BALANCE BY DATE + USER
        // =========================================================
        
        public BigDecimal getBalanceByDate(
                LocalDate date,
                String username) {
        
            return getIncomeByDate(
                    date,
                    username
            ).subtract(
                    getExpenseByDate(
                            date,
                            username
                    )
            );
        }

    // =========================================================
    // INCOME BY DATE RANGE
    // =========================================================

    public BigDecimal getIncomeByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        return transactionRepository
                .getTotalByDateRangeAndType(
                        startDate,
                        endDate,
                        TransactionType.INCOME
                );
    }

    // =========================================================
    // EXPENSE BY DATE RANGE
    // =========================================================

    public BigDecimal getExpenseByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        return transactionRepository
                .getTotalByDateRangeAndType(
                        startDate,
                        endDate,
                        TransactionType.EXPENSE
                );
    }

    // =========================================================
    // BALANCE BY DATE RANGE
    // =========================================================

    public BigDecimal getBalanceByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        return getIncomeByDateRange(
                startDate,
                endDate
        ).subtract(
                getExpenseByDateRange(
                        startDate,
                        endDate
                )
        );
    }
    
    // =========================================================
        // INCOME BY DATE RANGE + USER
        // =========================================================
        
        public BigDecimal getIncomeByDateRange(
                LocalDate startDate,
                LocalDate endDate,
                String username) {
        
            return transactionRepository
                    .getTotalByUserAndDateRangeAndType(
                            username,
                            startDate,
                            endDate,
                            TransactionType.INCOME
                    );
        }
        
        // =========================================================
        // EXPENSE BY DATE RANGE + USER
        // =========================================================
        
        public BigDecimal getExpenseByDateRange(
                LocalDate startDate,
                LocalDate endDate,
                String username) {
        
            return transactionRepository
                    .getTotalByUserAndDateRangeAndType(
                            username,
                            startDate,
                            endDate,
                            TransactionType.EXPENSE
                    );
        }
        
        // =========================================================
        // BALANCE BY DATE RANGE + USER
        // =========================================================
        
        public BigDecimal getBalanceByDateRange(
                LocalDate startDate,
                LocalDate endDate,
                String username) {
        
            return getIncomeByDateRange(
                    startDate,
                    endDate,
                    username
            ).subtract(
                    getExpenseByDateRange(
                            startDate,
                            endDate,
                            username
                    )
            );
        }
        
        // =========================================================
        // COUNT TRANSACTIONS BY DATE RANGE + USER
        // =========================================================
        
        public long countTransactionsByDateRange(
                LocalDate startDate,
                LocalDate endDate,
                String username) {
        
            return transactionRepository
                    .countByUserAndDateRange(
                            username,
                            startDate,
                            endDate
                    );
        }
        
        // =========================================================
        // DAILY SUMMARY BY USER
        // =========================================================
        
        public List<Object[]> getDailySummary(
                LocalDate startDate,
                LocalDate endDate,
                String username) {
        
            return transactionRepository
                    .getDailySummaryByUser(
                            username,
                            startDate,
                            endDate
                    );
        }

    // =========================================================
    // COUNT TRANSACTIONS
    // =========================================================

    public long countTransactionsByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        return transactionRepository
                .countByDateRange(
                        startDate,
                        endDate
                );
    }

    // =========================================================
    // DAILY SUMMARY
    // =========================================================

    public List<Object[]> getDailySummary(
            LocalDate startDate,
            LocalDate endDate) {

        return transactionRepository
                .getDailySummary(
                        startDate,
                        endDate
                );
    }

    // =========================================================
    // GENERATE TRANSACTION CODE
    // =========================================================

    private String generateTransactionCode() {

        String code;

        do {

            code =
                    "TRX-"
                    + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

        } while (
                transactionRepository
                        .existsByTransactionCode(code)
        );

        return code;
    }
    
    public Transaction getByAttachmentAndUser(
        String attachment,
        String username) {

    return transactionRepository
            .findByAttachmentAndCreatedBy(
                    attachment,
                    username
            )
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Attachment tidak ditemukan"
                    )
            );
}
}