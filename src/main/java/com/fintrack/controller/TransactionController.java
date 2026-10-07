package com.fintrack.controller;

import com.fintrack.entity.CategoryType;
import com.fintrack.entity.Transaction;
import com.fintrack.entity.TransactionType;
import com.fintrack.service.CategoryService;
import com.fintrack.service.FileStorageService;
import com.fintrack.service.FundSourceService;
import com.fintrack.service.TransactionService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final FundSourceService fundSourceService;
    private final FileStorageService fileStorageService;

    public TransactionController(
            TransactionService transactionService,
            CategoryService categoryService,
            FundSourceService fundSourceService,
            FileStorageService fileStorageService) {

        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.fundSourceService = fundSourceService;
        this.fileStorageService = fileStorageService;
    }

    // =========================================================
    // LIST
    // =========================================================

    @GetMapping
    public String index(
            Model model,
            Authentication authentication) {

        String username = authentication.getName();

        model.addAttribute(
                "pageTitle",
                "Transactions"
        );

        model.addAttribute(
                "pageSubtitle",
                "Kelola pemasukan dan pengeluaran FinTrack"
        );

        model.addAttribute(
                "activeMenu",
                "transactions"
        );

        /*
         * ADMIN melihat seluruh transaksi.
         * USER hanya melihat transaksi miliknya.
         */
        if (isAdmin(authentication)) {

            model.addAttribute(
                    "transactions",
                    transactionService.getAll()
            );

        } else {

            model.addAttribute(
                    "transactions",
                    transactionService.getAllByUser(
                            username
                    )
            );
        }

        return "transactions/index";
    }

    // =========================================================
    // FORM TAMBAH
    // =========================================================

    @GetMapping("/new")
    public String createForm(Model model) {

        Transaction transaction =
                new Transaction();

        transaction.setType(
                TransactionType.EXPENSE
        );

        model.addAttribute(
                "pageTitle",
                "Tambah Transaksi"
        );

        model.addAttribute(
                "pageSubtitle",
                "Catat pemasukan atau pengeluaran baru"
        );

        model.addAttribute(
                "activeMenu",
                "transactions"
        );

        model.addAttribute(
                "transaction",
                transaction
        );

        loadFormData(model);

        return "transactions/form";
    }

    // =========================================================
    // FORM EDIT
    // =========================================================

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        Transaction transaction;

        /*
         * ADMIN boleh edit semua transaksi.
         * USER hanya boleh edit transaksi miliknya.
         */
        if (isAdmin(authentication)) {

            transaction =
                    transactionService.getById(id);

        } else {

            transaction =
                    transactionService.getByIdAndUser(
                            id,
                            authentication.getName()
                    );
        }

        model.addAttribute(
                "pageTitle",
                "Edit Transaksi"
        );

        model.addAttribute(
                "pageSubtitle",
                "Perbarui informasi transaksi"
        );

        model.addAttribute(
                "activeMenu",
                "transactions"
        );

        model.addAttribute(
                "transaction",
                transaction
        );

        loadFormData(model);

        return "transactions/form";
    }

    // =========================================================
    // SAVE
    // =========================================================

    @PostMapping("/save")
    public String save(
            @ModelAttribute("transaction")
            Transaction transaction,

            @RequestParam(
                    value = "receipt",
                    required = false
            )
            MultipartFile receipt,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {

        try {

            String username =
                    authentication.getName();

            boolean admin =
                    isAdmin(authentication);

            // =================================================
            // CREATE
            // =================================================

            if (transaction.getId() == null) {

                /*
                 * createdBy WAJIB dari session login.
                 *
                 * Jangan pernah mengambil createdBy
                 * dari form HTML.
                 */
                transaction.setCreatedBy(
                        username
                );

                /*
                 * Upload bukti jika ada
                 */
                if (receipt != null
                        && !receipt.isEmpty()) {

                    String filename =
                            fileStorageService.storeFile(
                                    receipt
                            );

                    transaction.setAttachment(
                            filename
                    );
                }

                transactionService.save(
                        transaction
                );

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Transaksi berhasil ditambahkan."
                );

            }

            // =================================================
            // UPDATE
            // =================================================

            else {

                /*
                 * ADMIN boleh update transaksi siapa pun.
                 *
                 * USER hanya boleh update transaksi
                 * miliknya sendiri.
                 */
                Transaction existing;

                if (admin) {

                    existing =
                            transactionService.getById(
                                    transaction.getId()
                            );

                } else {

                    existing =
                            transactionService.getByIdAndUser(
                                    transaction.getId(),
                                    username
                            );
                }

                String oldAttachment =
                        existing.getAttachment();

                /*
                 * Jika upload file baru
                 */
                if (receipt != null
                        && !receipt.isEmpty()) {

                    String newFilename =
                            fileStorageService.storeFile(
                                    receipt
                            );

                    transaction.setAttachment(
                            newFilename
                    );

                    /*
                     * Hapus attachment lama
                     * setelah file baru berhasil disimpan.
                     */
                    if (oldAttachment != null
                            && !oldAttachment.isBlank()) {

                        fileStorageService.deleteFile(
                                oldAttachment
                        );
                    }

                } else {

                    /*
                     * Tidak upload file baru.
                     * Pertahankan attachment lama.
                     */
                    transaction.setAttachment(
                            oldAttachment
                    );
                }

                /*
                 * Jangan izinkan user mengubah owner.
                 */
                transaction.setCreatedBy(
                        existing.getCreatedBy()
                );

                /*
                 * ADMIN menggunakan save().
                 *
                 * USER menggunakan updateByUser()
                 * yang melakukan pengecekan ownership
                 * di service.
                 */
                if (admin) {

                    transactionService.save(
                            transaction
                    );

                } else {

                    transactionService.updateByUser(
                            transaction,
                            username
                    );
                }

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Transaksi berhasil diperbarui."
                );
            }

            return "redirect:/transactions";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/transactions";

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Gagal menyimpan transaksi: "
                            + e.getMessage()
            );

            return "redirect:/transactions";
        }
    }

    // =========================================================
    // DELETE
    // =========================================================

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            String username =
                    authentication.getName();

            boolean admin =
                    isAdmin(authentication);

            Transaction transaction;

            /*
             * ADMIN boleh menghapus transaksi siapa pun.
             *
             * USER hanya boleh menghapus transaksi
             * miliknya sendiri.
             */
            if (admin) {

                transaction =
                        transactionService.getById(id);

            } else {

                transaction =
                        transactionService.getByIdAndUser(
                                id,
                                username
                        );
            }

            /*
             * Hapus file attachment jika ada.
             */
            if (transaction.getAttachment() != null
                    && !transaction.getAttachment().isBlank()) {

                fileStorageService.deleteFile(
                        transaction.getAttachment()
                );
            }

            /*
             * Hapus transaksi.
             */
            if (admin) {

                transactionService.delete(id);

            } else {

                transactionService.deleteByUser(
                        id,
                        username
                );
            }

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Transaksi berhasil dihapus."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Gagal menghapus transaksi: "
                            + e.getMessage()
            );
        }

        return "redirect:/transactions";
    }

    // =========================================================
    // FORM DATA
    // =========================================================

    private void loadFormData(Model model) {

        model.addAttribute(
                "incomeCategories",
                categoryService.getByType(
                        CategoryType.INCOME
                )
        );

        model.addAttribute(
                "expenseCategories",
                categoryService.getByType(
                        CategoryType.EXPENSE
                )
        );

        model.addAttribute(
                "fundSources",
                fundSourceService.getAllActive()
        );
    }

    // =========================================================
    // VIEW ATTACHMENT
    // =========================================================

    @GetMapping("/attachment/{filename}")
    public ResponseEntity<Resource> viewAttachment(
            @PathVariable String filename) {

        try {

            Path filePath =
                    fileStorageService.getFile(filename);

            if (!Files.exists(filePath)) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );

            String contentType =
                    Files.probeContentType(filePath);

            if (contentType == null) {

                contentType =
                        MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity
                    .ok()
                    .contentType(
                            MediaType.parseMediaType(
                                    contentType
                            )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    filePath.getFileName() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    // =========================================================
    // CHECK ADMIN
    // =========================================================

    private boolean isAdmin(
            Authentication authentication) {

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority
                                .getAuthority()
                                .equals("ROLE_ADMIN")
                );
    }
}