package com.fintrack.controller;

import com.fintrack.service.ExcelReportService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/reports/export")
public class ExcelReportController {

    private final ExcelReportService excelReportService;

    public ExcelReportController(
            ExcelReportService excelReportService) {

        this.excelReportService =
                excelReportService;
    }

    // =========================================================
    // DAILY EXCEL
    // =========================================================

    @GetMapping("/daily")
    public ResponseEntity<byte[]> exportDaily(
            @RequestParam LocalDate date,
            Authentication authentication)
            throws IOException {

        String username =
                authentication.getName();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                "ROLE_ADMIN".equals(
                                        authority.getAuthority()
                                )
                        );

        byte[] excel;

        if (isAdmin) {

            excel =
                    excelReportService
                            .generateDailyReport(
                                    date
                            );

        } else {

            excel =
                    excelReportService
                            .generateDailyReport(
                                    date,
                                    username
                            );
        }

        String filename =
                "FinTrack-Daily-Report-"
                + date
                + ".xlsx";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                        + filename
                        + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }

    // =========================================================
    // MONTHLY EXCEL
    // =========================================================

    @GetMapping("/monthly")
    public ResponseEntity<byte[]> exportMonthly(
            @RequestParam int month,
            @RequestParam int year,
            Authentication authentication)
            throws IOException {

        YearMonth yearMonth =
                YearMonth.of(
                        year,
                        month
                );

        LocalDate startDate =
                yearMonth.atDay(1);

        LocalDate endDate =
                yearMonth.atEndOfMonth();

        String username =
                authentication.getName();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                "ROLE_ADMIN".equals(
                                        authority.getAuthority()
                                )
                        );

        byte[] excel;

        if (isAdmin) {

            excel =
                    excelReportService
                            .generateMonthlyReport(
                                    startDate,
                                    endDate
                            );

        } else {

            excel =
                    excelReportService
                            .generateMonthlyReport(
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
                + ".xlsx";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                        + filename
                        + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }
}