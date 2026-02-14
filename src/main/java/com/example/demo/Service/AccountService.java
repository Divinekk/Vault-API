package com.example.demo.Service;

import com.example.demo.DTO.AccountResponse;
import com.example.demo.DTO.AccountRequest;
import com.example.demo.Entity.BankAccount;
import com.example.demo.Entity.Users;
import com.example.demo.Exception.UnauthorizedAccessException;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        // Get authenticated user's email from SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate unique account number
        String accountNumber = accountNumberGenerator.generateUniqueAccountNumber();

        // Create account
        BankAccount account = new BankAccount();
        account.setOwner(user);
        account.setAccountNumber(accountNumber);
        account.setBalance(request.getInitialDeposit());

        BankAccount savedAccount = accountRepository.save(account);

        return mapToResponse(savedAccount);
    }

    public AccountResponse getAccount(Long accountId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // BOLA Prevention: Check ownership
        if (!account.getOwner().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("You are not authorized to access this account");
        }

        return mapToResponse(account);
    }

    public List<AccountResponse> getMyAccounts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<BankAccount> accounts = accountRepository.findByOwner_Email(email);

        return accounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AccountResponse mapToResponse(BankAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}