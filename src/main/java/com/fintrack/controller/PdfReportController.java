package com.fintrack.controller;

import com.fintrack.service.PdfReportService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/reports/export/pdf")
public class PdfReportController {

    private final PdfReportService pdfReportService;

    public PdfReportController(
            PdfReportService pdfReportService) {

        this.pdfReportService = pdfReportService;
    }

    // =========================================================
    // DAILY PDF
    // =========================================================

    @GetMapping("/daily")
    public ResponseEntity<byte[]> exportDaily(
            @RequestParam LocalDate date,
            Authentication authentication)
            throws Exception {

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));

        byte[] pdf;

        if (isAdmin) {

            pdf = pdfReportService.generateDailyReport(
                    date
            );

        } else {

            pdf = pdfReportService.generateDailyReport(
                    date,
                    username
            );
        }

        String filename =
                "FinTrack-Daily-Report-"
                + date
                + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + filename
                                + "\""
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }

    // =========================================================
    // MONTHLY PDF
    // =========================================================

    @GetMapping("/monthly")
    public ResponseEntity<byte[]> exportMonthly(
            @RequestParam int month,
            @RequestParam int year,
            Authentication authentication)
            throws Exception {

        YearMonth yearMonth =
                YearMonth.of(
                        year,
                        month
                );

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));

        byte[] pdf;

        if (isAdmin) {

            pdf = pdfReportService.generateMonthlyReport(
                    startDate,
                    endDate
            );

        } else {

            pdf = pdfReportService.generateMonthlyReport(
                    startDate,
                    endDate,
                    username
            );
        }

        String filename =
                "FinTrack-Monthly-Report-"
                + year
                + "-"
                + String.format(
                        "%02d",
                        month
                )
                + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + filename
                                + "\""
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(pdf);
    }
}