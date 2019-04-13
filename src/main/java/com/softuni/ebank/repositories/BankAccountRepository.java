package com.softuni.ebank.repositories;

import com.softuni.ebank.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Set<BankAccount> findAllByOwnerUsername(String username);
    BankAccount findByIban(String iban);
}


