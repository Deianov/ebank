package com.softuni.ebank.bindingModels;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BankAccountBindingModel {
    @NotNull
    @NotEmpty
    private Long id;
    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String iban;
    @NotNull
    @NotEmpty
    private BigDecimal amount;
    @NotNull
    @NotEmpty
    private Long receiverId;

    public BankAccountBindingModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
