package com.softuni.ebank.services;

import com.softuni.ebank.bindingModels.BankAccountBindingModel;
import com.softuni.ebank.entities.BankAccount;
import com.softuni.ebank.entities.Transaction;
import com.softuni.ebank.entities.User;
import com.softuni.ebank.repositories.BankAccountRepository;
import com.softuni.ebank.repositories.TransactionRepository;
import com.softuni.ebank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository,
                              UserRepository userRepository,
                              TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Set<BankAccount> findAllByOwnerUsername(Principal principal) {
        Set<BankAccount> bankAccounts = this.bankAccountRepository
                .findAllByOwnerUsername(principal.getName());

        return bankAccounts;
    }

    public boolean createAccount(BankAccountBindingModel bankAccountBindingModel) {
        if (bankAccountBindingModel.getIban() == null || bankAccountBindingModel.getIban().equals("")) {
            return false;
        }

        BankAccount bankAccount = this.bankAccountRepository.findByIban(bankAccountBindingModel.getIban());
        if (bankAccount != null) {
            return false;
        }

        User user = this.userRepository.findByUsername(bankAccountBindingModel.getUsername());
        if (user == null) {
            return false;
        }

        bankAccount = new BankAccount();
        bankAccount.setOwner(user);
        bankAccount.setIban(bankAccountBindingModel.getIban());
        bankAccount.setBalance(BigDecimal.ZERO);

        this.bankAccountRepository.save(bankAccount);
        return true;
    }

    public BankAccountBindingModel extractAccountForTransaction(Long id) {
        BankAccount bankAccount = this.bankAccountRepository.findById(id).orElse(null);
        if (bankAccount == null) {
            throw new IllegalArgumentException("Invalid Bank Account!");
        }

        BankAccountBindingModel bankAccountBindingModel = new BankAccountBindingModel();
        bankAccountBindingModel.setId(id);
        bankAccountBindingModel.setUsername(bankAccount.getOwner().getUsername());
        bankAccountBindingModel.setIban(bankAccount.getIban());

        return bankAccountBindingModel;
    }

    public boolean depositAmount(BankAccountBindingModel bankAccountBindingModel) {
        BankAccount bankAccount = this.bankAccountRepository.findById(bankAccountBindingModel.getId()).orElse(null);
        if (bankAccount == null) {
            return false;
        } else if (bankAccountBindingModel.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        bankAccount.setBalance(bankAccount.getBalance().add(bankAccountBindingModel.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setType("DEPOSIT");
        transaction.setFromAccount(bankAccount);
        transaction.setToAccount(bankAccount);
        transaction.setAmount(bankAccountBindingModel.getAmount());

        this.transactionRepository.save(transaction);
        this.bankAccountRepository.save(bankAccount);
        return true;
    }

    public boolean withdrawAmount(BankAccountBindingModel bankAccountBindingModel) {
        BankAccount bankAccount = this.bankAccountRepository.findById(bankAccountBindingModel.getId()).orElse(null);
        if (bankAccount == null) {
            return false;
        } else if (bankAccountBindingModel.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        bankAccount.setBalance(bankAccount.getBalance().subtract(bankAccountBindingModel.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setType("WITHDRAW");
        transaction.setFromAccount(bankAccount);
        transaction.setToAccount(bankAccount);
        transaction.setAmount(bankAccountBindingModel.getAmount());

        this.transactionRepository.save(transaction);
        this.bankAccountRepository.save(bankAccount);

        return true;
    }

    public boolean transferAmount(BankAccountBindingModel bankAccountBindingModel) {
        //Get bank account that sends amount
        BankAccount fromAccount = this.bankAccountRepository
                .findById(bankAccountBindingModel.getId()).orElse(null);

        //Get bank account that receives amount
        BankAccount toAccount = this.bankAccountRepository
                .findById(bankAccountBindingModel.getReceiverId()).orElse(null);

        //If one of the accounts does not exist, returns false
        if (fromAccount == null || toAccount == null) {
            return false;
        }
        //if the amount from the binding model's account is equal or less than 0, return false
        else if (bankAccountBindingModel.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        //Calculate the new balance for the bank account that sends amount and then set the new value
        BigDecimal newBalanceFromAccount = fromAccount.getBalance().subtract(bankAccountBindingModel.getAmount());
        fromAccount.setBalance(newBalanceFromAccount);

        //Returns false if the new balance in fromAccount is equal or less than 0
        if (fromAccount.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        //Calculate the new balanse for the bank account that receives amount and then set the new value
        BigDecimal newBalanceToAccount = toAccount.getBalance().add(bankAccountBindingModel.getAmount());
        toAccount.setBalance(newBalanceToAccount);

        //Create a new transaction and set its data
        Transaction transaction = new Transaction();
        transaction.setType("TRANSFER");
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(bankAccountBindingModel.getAmount());

        //Save changes and return true
        this.bankAccountRepository.save(fromAccount);
        this.bankAccountRepository.save(toAccount);
        this.transactionRepository.save(transaction);
        return true;
    }

    public Set<BankAccount> getAllBankAccountsForTransfer(Long id) {
        Set<BankAccount> bankAccounts = this.bankAccountRepository
                .findAll()
                .stream()
                .filter(ba -> !ba.getId().equals(id))
                .collect(Collectors.toSet());
        return bankAccounts;
    }
}


