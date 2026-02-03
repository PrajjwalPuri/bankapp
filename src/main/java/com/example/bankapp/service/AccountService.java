package com.example.bankapp.service;

import com.example.bankapp.model.Account;
import com.example.bankapp.model.Transaction;
import com.example.bankapp.repository.AccountRepository;
import com.example.bankapp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public Account findAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not exist"));
    }

    public Account registerAccount(String username, String password) {
        if(accountRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username Already Exists.");
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setBalance(BigDecimal.ZERO);
        HashSet<String> roles = new HashSet<>();
        roles.add("USER");
        account.setRoles(roles);
        return accountRepository.save(account);
    }

    // deposit the amount
    public void deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .type("Deposit")
                .timestamp(LocalDateTime.now())
                .build();


        transactionRepository.save(transaction);
    }

    public void withdraw(Account account, BigDecimal amount) {
        if(account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType("Withdrawal");
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Account account) {
        return transactionRepository.findByAccountId(account.getId());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Username or Password not found"));
    }

    public void transferAmount(Account fromAccount, String toUsername, BigDecimal amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }


        // deduct balance
        Account toAccount = accountRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        // add balance
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);

        // create debit transaction records
       Transaction debitTransaction = Transaction.builder()
               .amount(amount)
               .type("Transfer Out to " + toAccount.getUsername())
               .timestamp(LocalDateTime.now())
               .account(fromAccount)
               .build();
        transactionRepository.save(debitTransaction);

        // create debit transaction records
       Transaction creditTransaction = Transaction.builder()
               .amount(amount)
               .type("Transfer In from " + fromAccount.getUsername())
               .timestamp(LocalDateTime.now())
               .account(toAccount)
               .build();
        transactionRepository.save(creditTransaction);

    }
}
