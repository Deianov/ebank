package com.softuni.ebank.controllers;

import com.softuni.ebank.bindingModels.UserBindingModel;
import com.softuni.ebank.entities.Transaction;
import com.softuni.ebank.services.TransactionService;
import com.softuni.ebank.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Set;

@Controller
public class UserController {
    private final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    public UserController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public String register(@ModelAttribute("userBindingModel")UserBindingModel userBindingModel, Model model) {
        model.addAttribute("userBindingModel", userBindingModel);
        model.addAttribute("view", "users/register-user");
        return "fragments/layout";
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public String registerConfirm(@ModelAttribute("userBindingModel") UserBindingModel userBindingModel, Model model) {
        if (!this.userService.registerUser(userBindingModel)) {
            model.addAttribute("userBindingModel", userBindingModel);
            model.addAttribute("view", "user/register-user");
            return "fragments/layout";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public String login(@ModelAttribute("userBindingModel") UserBindingModel userBindingModel, Model model) {
        model.addAttribute("userBindingModel", userBindingModel);
        model.addAttribute("view", "users/login-user");
        return "fragments/layout";
    }

    @GetMapping("/transactions")
    @PreAuthorize("isAuthenticated()") //isAnonymous
    public String listTransactions(Principal principal, Model model) {
        String username = principal.getName();

        model.addAttribute("username", username);
        model.addAttribute("view", "users/transactions");

        Set<Transaction> transactions = this.transactionService.getTransactionsByName(username);
        model.addAttribute("transactions", transactions);

        return "fragments/layout";
    }
}
