package com.fintrack.controller;

import com.fintrack.service.TransactionService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class DailyReportController {

    private final TransactionService transactionService;

    public DailyReportController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/reports/daily")
    public String dailyReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Model model,
            Authentication authentication) {

        if (date == null) {
            date = LocalDate.now();
        }

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));

        // =====================================================
        // LAYOUT
        // =====================================================

        model.addAttribute(
                "pageTitle",
                "Daily Report"
        );

        model.addAttribute(
                "pageSubtitle",
                "Laporan transaksi harian FinTrack"
        );

        model.addAttribute(
                "activeMenu",
                "daily-report"
        );

        // =====================================================
        // REPORT DATA
        // =====================================================

        model.addAttribute(
                "selectedDate",
                date
        );

        /*
         * ADMIN
         * -----
         * Melihat seluruh transaksi.
         */
        if (isAdmin) {

            model.addAttribute(
                    "transactions",
                    transactionService.getTransactionsByDate(date)
            );

            model.addAttribute(
                    "totalIncome",
                    transactionService.getIncomeByDate(date)
            );

            model.addAttribute(
                    "totalExpense",
                    transactionService.getExpenseByDate(date)
            );

            model.addAttribute(
                    "balance",
                    transactionService.getBalanceByDate(date)
            );

        }

        /*
         * USER
         * ----
         * Hanya melihat transaksi miliknya sendiri.
         */
        else {

            model.addAttribute(
                    "transactions",
                    transactionService.getTransactionsByDate(
                            date,
                            username
                    )
            );

            model.addAttribute(
                    "totalIncome",
                    transactionService.getIncomeByDate(
                            date,
                            username
                    )
            );

            model.addAttribute(
                    "totalExpense",
                    transactionService.getExpenseByDate(
                            date,
                            username
                    )
            );

            model.addAttribute(
                    "balance",
                    transactionService.getBalanceByDate(
                            date,
                            username
                    )
            );
        }

        return "reports/daily";
    }
}