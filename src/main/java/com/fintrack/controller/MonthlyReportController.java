package com.fintrack.controller;

import com.fintrack.entity.TransactionType;
import com.fintrack.service.TransactionService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MonthlyReportController {

    private final TransactionService transactionService;

    public MonthlyReportController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @GetMapping("/reports/monthly")
    public String monthlyReport(
            @RequestParam(required = false)
            Integer month,

            @RequestParam(required = false)
            Integer year,

            Model model,
            Authentication authentication) {

        LocalDate today = LocalDate.now();

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));

        // =========================
        // DEFAULT BULAN & TAHUN
        // =========================

        if (month == null) {
            month = today.getMonthValue();
        }

        if (year == null) {
            year = today.getYear();
        }

        // =========================
        // PERIODE
        // =========================

        YearMonth yearMonth =
                YearMonth.of(year, month);

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        // =========================
        // SUMMARY
        // =========================

        BigDecimal totalIncome;

        BigDecimal totalExpense;

        long transactionCount;

        List<Object[]> dailySummary;

        if (isAdmin) {

            // ADMIN → SEMUA DATA

            totalIncome =
                    transactionService.getIncomeByDateRange(
                            startDate,
                            endDate
                    );

            totalExpense =
                    transactionService.getExpenseByDateRange(
                            startDate,
                            endDate
                    );

            transactionCount =
                    transactionService.countTransactionsByDateRange(
                            startDate,
                            endDate
                    );

            dailySummary =
                    transactionService.getDailySummary(
                            startDate,
                            endDate
                    );

        } else {

            // USER → DATA MILIK SENDIRI

            totalIncome =
                    transactionService.getIncomeByDateRange(
                            startDate,
                            endDate,
                            username
                    );

            totalExpense =
                    transactionService.getExpenseByDateRange(
                            startDate,
                            endDate,
                            username
                    );

            transactionCount =
                    transactionService.countTransactionsByDateRange(
                            startDate,
                            endDate,
                            username
                    );

            dailySummary =
                    transactionService.getDailySummary(
                            startDate,
                            endDate,
                            username
                    );
        }

        BigDecimal balance =
                totalIncome.subtract(totalExpense);

        // =========================
        // DAILY CHART DATA
        // =========================

        List<String> chartLabels =
                new ArrayList<>();

        List<BigDecimal> chartIncome =
                new ArrayList<>();

        List<BigDecimal> chartExpense =
                new ArrayList<>();

        // Buat label setiap hari
        for (
                LocalDate date = startDate;
                !date.isAfter(endDate);
                date = date.plusDays(1)
        ) {

            chartLabels.add(
                    String.valueOf(date.getDayOfMonth())
            );

            chartIncome.add(
                    BigDecimal.ZERO
            );

            chartExpense.add(
                    BigDecimal.ZERO
            );
        }

        // Masukkan data transaksi ke hari yang sesuai
        for (Object[] row : dailySummary) {

            LocalDate date =
                    (LocalDate) row[0];

            TransactionType type =
                    (TransactionType) row[1];

            BigDecimal amount =
                    (BigDecimal) row[2];

            int index =
                    date.getDayOfMonth() - 1;

            if (type == TransactionType.INCOME) {

                chartIncome.set(
                        index,
                        amount
                );

            } else if (type == TransactionType.EXPENSE) {

                chartExpense.set(
                        index,
                        amount
                );
            }
        }

        // =========================
        // TRANSACTION DATA
        // =========================

        List<?> transactions;

        if (isAdmin) {

            transactions =
                    transactionService.getTransactionsByDateRange(
                            startDate,
                            endDate
                    );

        } else {

            transactions =
                    transactionService.getTransactionsByDateRange(
                            startDate,
                            endDate,
                            username
                    );
        }

        // =========================
        // DATA UNTUK VIEW
        // =========================

        model.addAttribute(
                "pageTitle",
                "Monthly Report"
        );

        model.addAttribute(
                "pageSubtitle",
                "Laporan keuangan bulanan FinTrack"
        );

        model.addAttribute(
                "activeMenu",
                "monthly-report"
        );

        model.addAttribute(
                "selectedMonth",
                month
        );

        model.addAttribute(
                "selectedYear",
                year
        );

        model.addAttribute(
                "startDate",
                startDate
        );

        model.addAttribute(
                "endDate",
                endDate
        );

        model.addAttribute(
                "totalIncome",
                totalIncome
        );

        model.addAttribute(
                "totalExpense",
                totalExpense
        );

        model.addAttribute(
                "balance",
                balance
        );

        model.addAttribute(
                "transactionCount",
                transactionCount
        );

        model.addAttribute(
                "transactions",
                transactions
        );

        model.addAttribute(
                "chartLabels",
                chartLabels
        );

        model.addAttribute(
                "chartIncome",
                chartIncome
        );

        model.addAttribute(
                "chartExpense",
                chartExpense
        );

        // =========================
        // TAHUN DROPDOWN DINAMIS
        // =========================

        int currentYear = today.getYear();

        model.addAttribute(
                "yearStart",
                currentYear - 5
        );

        model.addAttribute(
                "yearEnd",
                currentYear + 5
        );

        return "reports/monthly";
    }
}