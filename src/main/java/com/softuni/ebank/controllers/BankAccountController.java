package com.softuni.ebank.controllers;

import com.softuni.ebank.bindingModels.BankAccountBindingModel;
import com.softuni.ebank.entities.BankAccount;
import com.softuni.ebank.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createBankAccount(@ModelAttribute("bankAccountBindingModel")BankAccountBindingModel bankAccountBindingModel,
                                    Principal principal, Model model) {
        bankAccountBindingModel.setUsername(principal.getName());

        model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);
        model.addAttribute("view", "accounts/create-account");

        return "fragments/layout";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createBankAccountConfirm(
            @ModelAttribute("bankAccountBindingModel") BankAccountBindingModel bankAccountBindingModel,
            Model model) {
        if (!this.bankAccountService.createAccount(bankAccountBindingModel)) {
            model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);
            model.addAttribute("view", "accounts/create-account");
            return "fragments/layout";
        }
        return "redirect:/home";
    }

    @GetMapping("/deposit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deposit(@PathVariable("id") Long id, Model model) {
        BankAccountBindingModel bankAccountBindingModel = this.bankAccountService.extractAccountForTransaction(id);
        model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);
        model.addAttribute("view", "accounts/deposit");

        return "fragments/layout";
    }

    @PostMapping("/deposit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String depositConfirm(@PathVariable("id") Long id, Model model,
                                 @ModelAttribute("bankAccountBindingModel") BankAccountBindingModel bankAccountBindingModel) {
        if (!this.bankAccountService.depositAmount(bankAccountBindingModel)) {
            model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);
            model.addAttribute("view", "accounts/deposit");
            return "fragments/layout";
        }
        return "redirect:/home";
    }

    @GetMapping("/withdraw/{id}")
    @PreAuthorize("isAuthenticated()")
    public String withdraw(@PathVariable("id") Long id, Model model) {
        BankAccountBindingModel bankAccountBindingModel = this.bankAccountService.extractAccountForTransaction(id);

        model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);
        model.addAttribute("view", "accounts/withdraw");

        return "fragments/layout";
    }

    @PostMapping("/withdraw/{id}")
    @PreAuthorize("isAuthenticated()")
    public String withdrawConfirm(@PathVariable("id") Long id, Model model,
                                  @ModelAttribute("bankAccountBindingModel") BankAccountBindingModel bankAccountBindingModel) {
        if (!this.bankAccountService.withdrawAmount(bankAccountBindingModel)) {
            model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);
            model.addAttribute("view", "accounts/withdraw");
            return "fragments/layout";
        }
        return "redirect:/home";
    }

    @GetMapping("/transfer/{id}")
    @PreAuthorize("isAuthenticated()")
    public String transfer(@PathVariable("id") Long id, Model model) {
        BankAccountBindingModel bankAccountBindingModel = this.bankAccountService.extractAccountForTransaction(id);

        model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);

        Set<BankAccount> bankAccounts = this.bankAccountService.getAllBankAccountsForTransfer(id);
        model.addAttribute("bankAccounts", bankAccounts);
        model.addAttribute("view", "accounts/transfer");
        return "fragments/layout";
    }

    @PostMapping("/transfer/{id}")
    @PreAuthorize("isAuthenticated()")
    public String transferConfirm(@PathVariable("id") Long id, Model model,
                                  @ModelAttribute("bankAccountBindingModel") BankAccountBindingModel bankAccountBindingModel) {
        if(!this.bankAccountService.transferAmount(bankAccountBindingModel)) {
            model.addAttribute("bankAccountBindingModel", bankAccountBindingModel);

            Set<BankAccount> bankAccounts = this.bankAccountService.getAllBankAccountsForTransfer(id);
            model.addAttribute("bankAccounts", bankAccounts);
            model.addAttribute("view", "accounts/transfer");
            return "fragments/layout";
        }
        return "redirect:/home";
    }
}
