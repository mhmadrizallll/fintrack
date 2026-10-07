package com.fintrack.service;

import com.fintrack.entity.Transaction;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportService {

    private final TransactionService transactionService;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public PdfReportService(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    // =========================================================
    // DAILY REPORT - ADMIN
    // =========================================================

    public byte[] generateDailyReport(
            LocalDate date) throws Exception {

        List<Transaction> transactions =
                transactionService.getTransactionsByDate(date);

        BigDecimal totalIncome =
                transactionService.getIncomeByDate(date);

        BigDecimal totalExpense =
                transactionService.getExpenseByDate(date);

        BigDecimal balance =
                totalIncome.subtract(totalExpense);

        return createPdf(
                "Laporan Keuangan Harian",
                "Tanggal: " + date.format(DATE_FORMAT),
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
            String username) throws Exception {

        List<Transaction> transactions =
                transactionService.getTransactionsByDate(
                        date,
                        username
                );

        BigDecimal totalIncome =
                transactionService.getIncomeByDate(
                        date,
                        username
                );

        BigDecimal totalExpense =
                transactionService.getExpenseByDate(
                        date,
                        username
                );

        BigDecimal balance =
                totalIncome.subtract(totalExpense);

        return createPdf(
                "Laporan Keuangan Harian",
                "Tanggal: " + date.format(DATE_FORMAT),
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
            LocalDate endDate) throws Exception {

        List<Transaction> transactions =
                transactionService.getTransactionsByDateRange(
                        startDate,
                        endDate
                );

        BigDecimal totalIncome =
                transactionService.getIncomeByDateRange(
                        startDate,
                        endDate
                );

        BigDecimal totalExpense =
                transactionService.getExpenseByDateRange(
                        startDate,
                        endDate
                );

        BigDecimal balance =
                totalIncome.subtract(totalExpense);

        String period =
                startDate.format(DATE_FORMAT)
                + " s/d "
                + endDate.format(DATE_FORMAT);

        return createPdf(
                "Laporan Keuangan Bulanan",
                "Periode: " + period,
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
            String username) throws Exception {

        List<Transaction> transactions =
                transactionService.getTransactionsByDateRange(
                        startDate,
                        endDate,
                        username
                );

        BigDecimal totalIncome =
                transactionService.getIncomeByDateRange(
                        startDate,
                        endDate,
                        username
                );

        BigDecimal totalExpense =
                transactionService.getExpenseByDateRange(
                        startDate,
                        endDate,
                        username
                );

        BigDecimal balance =
                totalIncome.subtract(totalExpense);

        String period =
                startDate.format(DATE_FORMAT)
                + " s/d "
                + endDate.format(DATE_FORMAT);

        return createPdf(
                "Laporan Keuangan Bulanan",
                "Periode: " + period,
                totalIncome,
                totalExpense,
                balance,
                transactions
        );
    }

    // =========================================================
    // CREATE PDF
    // =========================================================

    private byte[] createPdf(
            String title,
            String period,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal balance,
            List<Transaction> transactions)
            throws Exception {

        try (
                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            Document document =
                    new Document(
                            PageSize.A4,
                            36,
                            36,
                            36,
                            36
                    );

            PdfWriter.getInstance(
                    document,
                    output
            );

            document.open();

            // =================================================
            // TITLE
            // =================================================

            Font brandFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            22,
                            new Color(33, 37, 41)
                    );

            Paragraph brand =
                    new Paragraph(
                            "FINTRACK",
                            brandFont
                    );

            brand.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(brand);

            // =================================================
            // REPORT TITLE
            // =================================================

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            16,
                            new Color(52, 58, 64)
                    );

            Paragraph reportTitle =
                    new Paragraph(
                            title,
                            titleFont
                    );

            reportTitle.setAlignment(
                    Element.ALIGN_CENTER
            );

            reportTitle.setSpacingBefore(5);
            reportTitle.setSpacingAfter(4);

            document.add(reportTitle);

            // =================================================
            // PERIOD
            // =================================================

            Font periodFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10,
                            new Color(108, 117, 125)
                    );

            Paragraph periodParagraph =
                    new Paragraph(
                            period,
                            periodFont
                    );

            periodParagraph.setAlignment(
                    Element.ALIGN_CENTER
            );

            periodParagraph.setSpacingAfter(20);

            document.add(periodParagraph);

            // =================================================
            // SUMMARY TABLE
            // =================================================

            PdfPTable summaryTable =
                    new PdfPTable(3);

            summaryTable.setWidthPercentage(100);

            summaryTable.setSpacingAfter(20);

            summaryTable.setWidths(
                    new float[]{
                            1,
                            1,
                            1
                    }
            );

            addSummaryCell(
                    summaryTable,
                    "TOTAL INCOME",
                    formatCurrency(totalIncome),
                    new Color(40, 167, 69)
            );

            addSummaryCell(
                    summaryTable,
                    "TOTAL EXPENSE",
                    formatCurrency(totalExpense),
                    new Color(220, 53, 69)
            );

            addSummaryCell(
                    summaryTable,
                    "BALANCE",
                    formatCurrency(balance),
                    new Color(0, 123, 255)
            );

            document.add(summaryTable);

            // =================================================
            // TRANSACTION TITLE
            // =================================================

            Font sectionFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12,
                            new Color(33, 37, 41)
                    );

            Paragraph transactionTitle =
                    new Paragraph(
                            "Detail Transaksi",
                            sectionFont
                    );

            transactionTitle.setSpacingAfter(8);

            document.add(transactionTitle);

            // =================================================
            // TRANSACTION TABLE
            // =================================================

            PdfPTable table =
                    new PdfPTable(7);

            table.setWidthPercentage(100);

            table.setWidths(
                    new float[]{
                            0.5f,
                            1.1f,
                            1.5f,
                            1.1f,
                            1.3f,
                            1.4f,
                            2.0f
                    }
            );

            addHeaderCell(table, "No");
            addHeaderCell(table, "Tanggal");
            addHeaderCell(table, "Kode");
            addHeaderCell(table, "Tipe");
            addHeaderCell(table, "Kategori");
            addHeaderCell(table, "Sumber Dana");
            addHeaderCell(table, "Jumlah");

            int number = 1;

            for (Transaction transaction : transactions) {

                addBodyCell(
                        table,
                        String.valueOf(number++)
                );

                addBodyCell(
                        table,
                        transaction.getTransactionDate()
                                .format(DATE_FORMAT)
                );

                addBodyCell(
                        table,
                        transaction.getTransactionCode()
                );

                addBodyCell(
                        table,
                        transaction.getType().name()
                );

                addBodyCell(
                        table,
                        transaction.getCategory() != null
                                ? transaction.getCategory().getName()
                                : "-"
                );

                addBodyCell(
                        table,
                        transaction.getFundSource() != null
                                ? transaction.getFundSource().getName()
                                : "-"
                );

                addBodyCell(
                        table,
                        formatCurrency(
                                transaction.getAmount()
                        )
                );
            }

            if (transactions.isEmpty()) {

                PdfPCell emptyCell =
                        new PdfPCell(
                                new Phrase(
                                        "Tidak ada transaksi pada periode ini."
                                )
                        );

                emptyCell.setColspan(7);

                emptyCell.setHorizontalAlignment(
                        Element.ALIGN_CENTER
                );

                emptyCell.setPadding(10);

                table.addCell(emptyCell);
            }

            document.add(table);

            // =================================================
            // FOOTER
            // =================================================

            Paragraph footer =
                    new Paragraph(
                            "Generated by FinTrack",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA,
                                    8,
                                    new Color(108, 117, 125)
                            )
                    );

            footer.setAlignment(
                    Element.ALIGN_CENTER
            );

            footer.setSpacingBefore(20);

            document.add(footer);

            document.close();

            return output.toByteArray();
        }
    }

    // =========================================================
    // SUMMARY CELL
    // =========================================================

    private void addSummaryCell(
            PdfPTable table,
            String label,
            String value,
            Color color) {

        PdfPCell cell =
                new PdfPCell();

        cell.setPadding(10);

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        cell.setBorderColor(
                new Color(222, 226, 230)
        );

        Font labelFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        8,
                        new Color(108, 117, 125)
                );

        Font valueFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        13,
                        color
                );

        Paragraph paragraph =
                new Paragraph();

        paragraph.setAlignment(
                Element.ALIGN_CENTER
        );

        paragraph.add(
                new Chunk(
                        label + "\n",
                        labelFont
                )
        );

        paragraph.add(
                new Chunk(
                        value,
                        valueFont
                )
        );

        cell.addElement(paragraph);

        table.addCell(cell);
    }

    // =========================================================
    // HEADER CELL
    // =========================================================

    private void addHeaderCell(
            PdfPTable table,
            String text) {

        Font font =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        8,
                        Color.WHITE
                );

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(text, font)
                );

        cell.setBackgroundColor(
                new Color(52, 58, 64)
        );

        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        cell.setPadding(6);

        table.addCell(cell);
    }

    // =========================================================
    // BODY CELL
    // =========================================================

    private void addBodyCell(
            PdfPTable table,
            String text) {

        Font font =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        7
                );

        PdfPCell cell =
                new PdfPCell(
                        new Phrase(
                                text != null ? text : "",
                                font
                        )
                );

        cell.setPadding(5);

        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        table.addCell(cell);
    }

    // =========================================================
    // CURRENCY
    // =========================================================

    private String formatCurrency(
            BigDecimal amount) {

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        return "Rp "
                + String.format(
                        "%,.0f",
                        amount
                );
    }
}