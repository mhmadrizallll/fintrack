package com.fintrack.controller;

import com.fintrack.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final TransactionService transactionService;

    public DashboardController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Authentication authentication) {

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));

        model.addAttribute(
                "pageTitle",
                "Dashboard"
        );

        model.addAttribute(
                "pageSubtitle",
                "Ringkasan kondisi keuangan FinTrack"
        );

        model.addAttribute(
                "activeMenu",
                "dashboard"
        );

        /*
         * ============================
         * ADMIN
         * ============================
         *
         * Admin melihat seluruh transaksi.
         */
        if (isAdmin) {

            model.addAttribute(
                    "totalIncome",
                    transactionService.getTotalIncome()
            );

            model.addAttribute(
                    "totalExpense",
                    transactionService.getTotalExpense()
            );

            model.addAttribute(
                    "balance",
                    transactionService.getBalance()
            );

            model.addAttribute(
                    "todayIncome",
                    transactionService.getTodayIncome()
            );

            model.addAttribute(
                    "todayExpense",
                    transactionService.getTodayExpense()
            );

            model.addAttribute(
                    "recentTransactions",
                    transactionService.getRecentTransactions()
            );

        }

        /*
         * ============================
         * USER
         * ============================
         *
         * User hanya melihat transaksi
         * miliknya sendiri.
         */
        else {

            model.addAttribute(
                    "totalIncome",
                    transactionService.getTotalIncomeByUser(username)
            );

            model.addAttribute(
                    "totalExpense",
                    transactionService.getTotalExpenseByUser(username)
            );

            model.addAttribute(
                    "balance",
                    transactionService.getBalanceByUser(username)
            );

            model.addAttribute(
                    "todayIncome",
                    transactionService.getTodayIncomeByUser(username)
            );

            model.addAttribute(
                    "todayExpense",
                    transactionService.getTodayExpenseByUser(username)
            );

            model.addAttribute(
                    "recentTransactions",
                    transactionService.getRecentTransactionsByUser(username)
            );
        }

        return "dashboard";
    }
}