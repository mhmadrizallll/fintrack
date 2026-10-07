package com.fintrack.service;

import com.fintrack.entity.FundSource;
import com.fintrack.repository.FundSourceRepository;
import com.fintrack.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundSourceService {

    private final FundSourceRepository fundSourceRepository;
    private final TransactionRepository transactionRepository;

    public FundSourceService(
            FundSourceRepository fundSourceRepository,
            TransactionRepository transactionRepository) {

        this.fundSourceRepository =
                fundSourceRepository;

        this.transactionRepository =
                transactionRepository;
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
                                "Fund Source tidak ditemukan: "
                                        + id
                        )
                );
    }

    // =========================================================
    // SAVE
    // CREATE / UPDATE
    // =========================================================

    public FundSource save(
            FundSource fundSource) {

        // =====================================================
        // CREATE
        // =====================================================

        if (fundSource.getId() == null) {

            if (fundSource.getStatus() == null
                    || fundSource.getStatus().isBlank()) {

                fundSource.setStatus("ACTIVE");
            }

            return fundSourceRepository.save(
                    fundSource
            );
        }

        // =====================================================
        // UPDATE
        // =====================================================

        FundSource existing =
                fundSourceRepository
                        .findById(
                                fundSource.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Fund Source tidak ditemukan: "
                                                + fundSource.getId()
                                )
                        );

        existing.setName(
                fundSource.getName()
        );

        existing.setType(
                fundSource.getType()
        );

        existing.setDescription(
                fundSource.getDescription()
        );

        if (fundSource.getStatus() != null
                && !fundSource.getStatus().isBlank()) {

            existing.setStatus(
                    fundSource.getStatus()
            );
        }

        return fundSourceRepository.save(
                existing
        );
    }

    // =========================================================
    // DELETE / DEACTIVATE
    // =========================================================

    public void deactivate(Long id) {

        // -----------------------------------------------------
        // Pastikan fund source ada
        // -----------------------------------------------------

        FundSource fundSource =
                getById(id);

        // -----------------------------------------------------
        // Cek apakah sudah digunakan transaksi
        // -----------------------------------------------------

        boolean used =
                transactionRepository
                        .existsByFundSourceId(id);

        if (used) {

            throw new IllegalStateException(
                    "Fund Source \""
                            + fundSource.getName()
                            + "\" tidak dapat dihapus "
                            + "karena sudah digunakan "
                            + "pada transaksi."
            );
        }

        // -----------------------------------------------------
        // Kalau belum digunakan → INACTIVE
        // -----------------------------------------------------

        fundSource.setStatus("INACTIVE");

        fundSourceRepository.save(
                fundSource
        );
    }
}