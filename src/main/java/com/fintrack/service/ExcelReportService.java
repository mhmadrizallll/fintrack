package com.fintrack.service;

import com.fintrack.entity.Transaction;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExcelReportService {

    private final TransactionService transactionService;

    public ExcelReportService(
            TransactionService transactionService) {

        this.transactionService =
                transactionService;
    }

    // =========================================================
    // DAILY REPORT - ADMIN
    // =========================================================

    public byte[] generateDailyReport(
            LocalDate date) throws IOException {

        List<Transaction> transactions =
                transactionService
                        .getTransactionsByDate(date);

        BigDecimal totalIncome =
                transactionService
                        .getIncomeByDate(date);

        BigDecimal totalExpense =
                transactionService
                        .getExpenseByDate(date);

        BigDecimal balance =
                totalIncome.subtract(
                        totalExpense
                );

        return createExcel(
                "Daily Report",
                date.toString(),
                totalIncome,
                totalExpense,
                balance,
                transactions
        );
    }

    // =========================================================
    // DAILY REPORT - USER
    // =========================================================

    public byte[] generateDailyReport(
            LocalDate date,
            String username) throws IOException {

        List<Transaction> transactions =
                transactionService
                        .getTransactionsByDate(
                                date,
                                username
                        );

        BigDecimal totalIncome =
                transactionService
                        .getIncomeByDate(
                                date,
                                username
                        );

        BigDecimal totalExpense =
                transactionService
                        .getExpenseByDate(
                                date,
                                username
                        );

        BigDecimal balance =
                totalIncome.subtract(
                        totalExpense
                );

        return createExcel(
                "Daily Report",
                date.toString(),
                totalIncome,
                totalExpense,
                balance,
                transactions
        );
    }

    // =========================================================
    // MONTHLY REPORT - ADMIN
    // =========================================================

    public byte[] generateMonthlyReport(
            LocalDate startDate,
            LocalDate endDate) throws IOException {

        List<Transaction> transactions =
                transactionService
                        .getTransactionsByDateRange(
                                startDate,
                                endDate
                        );

        BigDecimal totalIncome =
                transactionService
                        .getIncomeByDateRange(
                                startDate,
                                endDate
                        );

        BigDecimal totalExpense =
                transactionService
                        .getExpenseByDateRange(
                                startDate,
                                endDate
                        );

        BigDecimal balance =
                totalIncome.subtract(
                        totalExpense
                );

        return createExcel(
                "Monthly Report",
                startDate + " s/d " + endDate,
                totalIncome,
                totalExpense,
                balance,
                transactions
        );
    }

    // =========================================================
    // MONTHLY REPORT - USER
    // =========================================================

    public byte[] generateMonthlyReport(
            LocalDate startDate,
            LocalDate endDate,
            String username) throws IOException {

        List<Transaction> transactions =
                transactionService
                        .getTransactionsByDateRange(
                                startDate,
                                endDate,
                                username
                        );

        BigDecimal totalIncome =
                transactionService
                        .getIncomeByDateRange(
                                startDate,
                                endDate,
                                username
                        );

        BigDecimal totalExpense =
                transactionService
                        .getExpenseByDateRange(
                                startDate,
                                endDate,
                                username
                        );

        BigDecimal balance =
                totalIncome.subtract(
                        totalExpense
                );

        return createExcel(
                "Monthly Report",
                startDate + " s/d " + endDate,
                totalIncome,
                totalExpense,
                balance,
                transactions
        );
    }

    // =========================================================
    // CREATE EXCEL
    // =========================================================

    private byte[] createExcel(
            String reportTitle,
            String period,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal balance,
            List<Transaction> transactions)
            throws IOException {

        try (
                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet(
                            "Report"
                    );

            // =================================================
            // COLUMN WIDTH
            // =================================================

            sheet.setColumnWidth(
                    0,
                    6 * 256
            );

            sheet.setColumnWidth(
                    1,
                    20 * 256
            );

            sheet.setColumnWidth(
                    2,
                    15 * 256
            );

            sheet.setColumnWidth(
                    3,
                    14 * 256
            );

            sheet.setColumnWidth(
                    4,
                    20 * 256
            );

            sheet.setColumnWidth(
                    5,
                    20 * 256
            );

            sheet.setColumnWidth(
                    6,
                    20 * 256
            );

            sheet.setColumnWidth(
                    7,
                    35 * 256
            );

            sheet.setColumnWidth(
                    8,
                    20 * 256
            );

            // =================================================
            // TITLE STYLE
            // =================================================

            Font titleFont =
                    workbook.createFont();

            titleFont.setBold(true);

            titleFont.setFontHeightInPoints(
                    (short) 16
            );

            CellStyle titleStyle =
                    workbook.createCellStyle();

            titleStyle.setFont(
                    titleFont
            );

            // =================================================
            // HEADER STYLE
            // =================================================

            Font headerFont =
                    workbook.createFont();

            headerFont.setBold(true);

            CellStyle headerStyle =
                    workbook.createCellStyle();

            headerStyle.setFont(
                    headerFont
            );

            headerStyle.setFillForegroundColor(
                    IndexedColors.GREY_25_PERCENT
                            .getIndex()
            );

            headerStyle.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            headerStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            // =================================================
            // CURRENCY STYLE
            // =================================================

            CellStyle currencyStyle =
                    workbook.createCellStyle();

            currencyStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat(
                                    "\"Rp \"#,##0"
                            )
            );

            // =================================================
            // DATE STYLE
            // =================================================

            CellStyle dateStyle =
                    workbook.createCellStyle();

            dateStyle.setDataFormat(
                    workbook
                            .createDataFormat()
                            .getFormat(
                                    "dd-mm-yyyy"
                            )
            );

            // =================================================
            // TITLE
            // =================================================

            Row titleRow =
                    sheet.createRow(0);

            Cell titleCell =
                    titleRow.createCell(0);

            titleCell.setCellValue(
                    reportTitle
            );

            titleCell.setCellStyle(
                    titleStyle
            );

            sheet.addMergedRegion(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0,
                            0,
                            0,
                            8
                    )
            );

            // =================================================
            // PERIOD
            // =================================================

            Row periodRow =
                    sheet.createRow(1);

            periodRow.createCell(0)
                    .setCellValue(
                            "Periode"
                    );

            periodRow.createCell(1)
                    .setCellValue(
                            period
                    );

            // =================================================
            // SUMMARY
            // =================================================

            Row summaryHeader =
                    sheet.createRow(3);

            summaryHeader
                    .createCell(0)
                    .setCellValue(
                            "Summary"
                    );

            summaryHeader
                    .getCell(0)
                    .setCellStyle(
                            headerStyle
                    );

            // =================================================
            // TOTAL INCOME
            // =================================================

            Row incomeRow =
                    sheet.createRow(4);

            incomeRow
                    .createCell(0)
                    .setCellValue(
                            "Total Income"
                    );

            incomeRow
                    .createCell(1)
                    .setCellValue(
                            totalIncome.doubleValue()
                    );

            incomeRow
                    .getCell(1)
                    .setCellStyle(
                            currencyStyle
                    );

            // =================================================
            // TOTAL EXPENSE
            // =================================================

            Row expenseRow =
                    sheet.createRow(5);

            expenseRow
                    .createCell(0)
                    .setCellValue(
                            "Total Expense"
                    );

            expenseRow
                    .createCell(1)
                    .setCellValue(
                            totalExpense.doubleValue()
                    );

            expenseRow
                    .getCell(1)
                    .setCellStyle(
                            currencyStyle
                    );

            // =================================================
            // BALANCE
            // =================================================

            Row balanceRow =
                    sheet.createRow(6);

            balanceRow
                    .createCell(0)
                    .setCellValue(
                            "Balance"
                    );

            balanceRow
                    .createCell(1)
                    .setCellValue(
                            balance.doubleValue()
                    );

            balanceRow
                    .getCell(1)
                    .setCellStyle(
                            currencyStyle
                    );

            // =================================================
            // TABLE HEADER
            // =================================================

            Row tableHeader =
                    sheet.createRow(8);

            String[] headers = {
                    "No",
                    "Transaction Code",
                    "Date",
                    "Type",
                    "Category",
                    "Fund Source",
                    "Amount",
                    "Description",
                    "Created By"
            };

            for (
                    int i = 0;
                    i < headers.length;
                    i++
            ) {

                Cell cell =
                        tableHeader.createCell(i);

                cell.setCellValue(
                        headers[i]
                );

                cell.setCellStyle(
                        headerStyle
                );
            }

            // =================================================
            // TRANSACTION DATA
            // =================================================

            int rowIndex = 9;

            int number = 1;

            for (
                    Transaction transaction :
                    transactions
            ) {

                Row row =
                        sheet.createRow(
                                rowIndex++
                        );

                // =================================================
                // NO
                // =================================================

                row.createCell(0)
                        .setCellValue(
                                number++
                        );

                // =================================================
                // TRANSACTION CODE
                // =================================================

                row.createCell(1)
                        .setCellValue(
                                transaction
                                        .getTransactionCode()
                        );

                // =================================================
                // DATE
                // =================================================

                Cell dateCell =
                        row.createCell(2);

                dateCell.setCellValue(
                        transaction
                                .getTransactionDate()
                );

                dateCell.setCellStyle(
                        dateStyle
                );

                // =================================================
                // TYPE
                // =================================================

                row.createCell(3)
                        .setCellValue(
                                transaction
                                        .getType()
                                        .name()
                        );

                // =================================================
                // CATEGORY
                // =================================================

                row.createCell(4)
                        .setCellValue(
                                transaction.getCategory() != null
                                        ? transaction
                                                .getCategory()
                                                .getName()
                                        : "-"
                        );

                // =================================================
                // FUND SOURCE
                // =================================================

                row.createCell(5)
                        .setCellValue(
                                transaction.getFundSource() != null
                                        ? transaction
                                                .getFundSource()
                                                .getName()
                                        : "-"
                        );

                // =================================================
                // AMOUNT
                // =================================================

                Cell amountCell =
                        row.createCell(6);

                amountCell.setCellValue(
                        transaction
                                .getAmount()
                                .doubleValue()
                );

                amountCell.setCellStyle(
                        currencyStyle
                );

                // =================================================
                // DESCRIPTION
                // =================================================

                row.createCell(7)
                        .setCellValue(
                                transaction
                                        .getDescription() != null
                                        ? transaction
                                                .getDescription()
                                        : ""
                        );

                // =================================================
                // CREATED BY
                // =================================================

                row.createCell(8)
                        .setCellValue(
                                transaction
                                        .getCreatedBy() != null
                                        ? transaction
                                                .getCreatedBy()
                                        : ""
                        );
            }

            // =================================================
            // FREEZE HEADER
            // =================================================

            sheet.createFreezePane(
                    0,
                    9
            );

            // =================================================
            // AUTO FILTER
            // =================================================

            if (!transactions.isEmpty()) {

                sheet.setAutoFilter(
                        new org.apache.poi.ss.util.CellRangeAddress(
                                8,
                                rowIndex - 1,
                                0,
                                8
                        )
                );
            }

            // =================================================
            // WRITE EXCEL
            // =================================================

            workbook.write(
                    output
            );

            return output.toByteArray();
        }
    }
}