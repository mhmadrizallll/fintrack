
package com.fintrack.service;

import com.fintrack.entity.FundSource;
import com.fintrack.entity.TransactionType;
import com.fintrack.repository.FundSourceRepository;
import com.fintrack.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FundSourceService {

    private final FundSourceRepository fundSourceRepository;
    private final TransactionRepository transactionRepository;

    public FundSourceService(
            FundSourceRepository fundSourceRepository,
            TransactionRepository transactionRepository) {

        this.fundSourceRepository = fundSourceRepository;
        this.transactionRepository = transactionRepository;
    }

    // =========================================================
    // GET ALL ACTIVE
    // =========================================================

    public List<FundSource> getAllActive() {
        return fundSourceRepository
                .findByStatusOrderByNameAsc("ACTIVE");
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    public FundSource getById(Long id) {
        return fundSourceRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Fund Source tidak ditemukan: " + id
                        )
                );
    }

    // =========================================================
    // GET CURRENT BALANCE - ALL TRANSACTIONS
    // =========================================================

    public BigDecimal getBalance(Long fundSourceId) {

        getById(fundSourceId);

        return transactionRepository.calculateBalanceByFundSource(
                fundSourceId,
                TransactionType.INCOME,
                TransactionType.EXPENSE,
                TransactionType.TRANSFER
        );
    }

    // =========================================================
    // GET CURRENT BALANCE - BY USER
    // =========================================================

    public BigDecimal getBalanceByUser(
            Long fundSourceId,
            String username) {

        getById(fundSourceId);

        return transactionRepository
                .calculateBalanceByFundSourceAndUser(
                        fundSourceId,
                        username,
                        TransactionType.INCOME,
                        TransactionType.EXPENSE,
                        TransactionType.TRANSFER
                );
    }

    // =========================================================
    // GET ALL ACTIVE WITH BALANCE
    // =========================================================

    public List<FundSourceBalance> getAllActiveWithBalance() {

        return getAllActive()
                .stream()
                .map(fundSource -> new FundSourceBalance(
                        fundSource,
                        getBalance(fundSource.getId())
                ))
                .toList();
    }

    // =========================================================
    // SAVE - CREATE / UPDATE
    // =========================================================

    @Transactional
    public FundSource save(FundSource fundSource) {

        // CREATE
        if (fundSource.getId() == null) {

            if (fundSource.getStatus() == null
                    || fundSource.getStatus().isBlank()) {

                fundSource.setStatus("ACTIVE");
            }

            return fundSourceRepository.save(fundSource);
        }

        // UPDATE
        FundSource existing = getById(fundSource.getId());

        existing.setName(fundSource.getName());
        existing.setType(fundSource.getType());
        existing.setDescription(fundSource.getDescription());

        if (fundSource.getStatus() != null
                && !fundSource.getStatus().isBlank()) {

            existing.setStatus(fundSource.getStatus());
        }

        return fundSourceRepository.save(existing);
    }

    // =========================================================
    // DEACTIVATE
    // =========================================================

    @Transactional
    public void deactivate(Long id) {

        FundSource fundSource = getById(id);

        boolean usedAsSource =
                transactionRepository.existsByFundSourceId(id);

        boolean usedAsDestination =
                transactionRepository.existsByDestinationFundSourceId(id);

        if (usedAsSource || usedAsDestination) {
            throw new IllegalStateException(
                    "Fund Source \"" + fundSource.getName()
                            + "\" tidak dapat dinonaktifkan karena "
                            + "sudah digunakan pada transaksi."
            );
        }

        fundSource.setStatus("INACTIVE");
        fundSourceRepository.save(fundSource);
    }

    // =========================================================
    // BALANCE RESULT
    // =========================================================

    public record FundSourceBalance(
            FundSource fundSource,
            BigDecimal balance) {
    }
}
