package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepo;

    @Autowired
    public AccountService(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    public Account register(Account toRegister) {
        if (toRegister.getUsername() == null || toRegister.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank.");
        }
        if (toRegister.getPassword() == null || toRegister.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        if (accountRepo.existsByUsername(toRegister.getUsername())) {
            throw new DuplicateUsernameException("Username already exists.");
        }

        return accountRepo.save(toRegister);
    }

 
    public Account login(Account candidate) {
        if (candidate.getUsername() == null || candidate.getPassword() == null) {
            throw new AuthenticationException("Username and password must be provided.");
        }

        Optional<Account> fetched = accountRepo.findByUsername(candidate.getUsername());
        if (!fetched.isPresent()) {
            throw new AuthenticationException("Invalid username or password.");
        }
        Account stored = fetched.get();
        if (!stored.getPassword().equals(candidate.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }
        return stored;
    }

    // Custom exceptions to distinguish 409 vs 401 vs 400
    public static class DuplicateUsernameException extends RuntimeException {
        public DuplicateUsernameException(String msg) {
            super(msg);
        }
    }
    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String msg) {
            super(msg);
        }
    }
}
