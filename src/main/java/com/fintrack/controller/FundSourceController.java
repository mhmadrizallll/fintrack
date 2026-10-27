
package com.fintrack.controller;

import com.fintrack.entity.FundSource;
import com.fintrack.service.FundSourceService;
import com.fintrack.service.TransactionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/fund-sources")
public class FundSourceController {

    private final FundSourceService fundSourceService;
    private final TransactionService transactionService;

    public FundSourceController(
            FundSourceService fundSourceService,
            TransactionService transactionService) {

        this.fundSourceService = fundSourceService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String index(Model model) {

        List<FundSource> fundSources =
                fundSourceService.getAllActive();

        Map<Long, BigDecimal> balances = new LinkedHashMap<>();

        BigDecimal totalBalance = BigDecimal.ZERO;

        for (FundSource fundSource : fundSources) {

            BigDecimal balance =
                    transactionService.getBalanceByFundSource(
                            fundSource.getId()
                    );

            if (balance == null) {
                balance = BigDecimal.ZERO;
            }

            balances.put(fundSource.getId(), balance);
            totalBalance = totalBalance.add(balance);
        }

        model.addAttribute("pageTitle", "Fund Sources");
        model.addAttribute(
                "pageSubtitle",
                "Kelola sumber dana FinTrack"
        );
        model.addAttribute("activeMenu", "fund-sources");
        model.addAttribute("fundSources", fundSources);
        model.addAttribute("balances", balances);
        model.addAttribute("totalBalance", totalBalance);

        return "fund-sources/index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("pageTitle", "Tambah Fund Source");
        model.addAttribute(
                "pageSubtitle",
                "Tambahkan sumber dana baru"
        );
        model.addAttribute("activeMenu", "fund-sources");
        model.addAttribute("fundSource", new FundSource());

        return "fund-sources/form";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute("fundSource") FundSource fundSource,
            RedirectAttributes redirectAttributes) {

        fundSourceService.save(fundSource);

        redirectAttributes.addFlashAttribute(
                "success",
                "Fund Source berhasil disimpan."
        );

        return "redirect:/admin/fund-sources";
    }

    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("pageTitle", "Edit Fund Source");
        model.addAttribute(
                "pageSubtitle",
                "Perbarui informasi sumber dana"
        );
        model.addAttribute("activeMenu", "fund-sources");
        model.addAttribute(
                "fundSource",
                fundSourceService.getById(id)
        );

        return "fund-sources/form";
    }

    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            fundSourceService.deactivate(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Fund Source berhasil dinonaktifkan."
            );

        } catch (IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/fund-sources";
    }
}
