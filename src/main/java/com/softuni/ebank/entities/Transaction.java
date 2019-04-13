package com.softuni.ebank.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;
    @Column(name = "transaction_type")
    private String type;
    @ManyToOne(targetEntity = BankAccount.class)
    @JoinColumn(name = "sender", updatable = false)
    private BankAccount fromAccount;
    @ManyToOne(targetEntity = BankAccount.class)
    @JoinColumn(name = "receiver", updatable = false)
    private BankAccount toAccount;
    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BankAccount getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(BankAccount fromAccount) {
        this.fromAccount = fromAccount;
    }

    public BankAccount getToAccount() {
        return toAccount;
    }

    public void setToAccount(BankAccount toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
