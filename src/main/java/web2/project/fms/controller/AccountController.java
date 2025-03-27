package web2.project.fms.controller;

import web2.project.fms.model.Account;
import web2.project.fms.security.SecurityUtils;
import web2.project.fms.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import web2.project.fms.service.TransactionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable UUID id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/me")
    public ResponseEntity<Account> getOwnAccount() {
        Account account = accountService.getAccountById(SecurityUtils.getAuthenticatedAccountId());
        return ResponseEntity.ok(account);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.ok(createdAccount);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Account> updateAccount(@PathVariable UUID id, @RequestBody Account accountDetails) {
        SecurityUtils.checkOwnershipOrAdmin(id);
        Account updatedAccount = accountService.updateAccount(id, accountDetails);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        SecurityUtils.checkOwnershipOrAdmin(id);
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> transferFunds(@RequestParam UUID fromAccountId, @RequestParam UUID toAccountId, @RequestParam double amount) {
        SecurityUtils.checkOwnershipOrAdmin(fromAccountId);
        transactionService.transferFunds(fromAccountId, toAccountId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance-above")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> getAccountsByBalanceAbove(@RequestParam double amount) {
        return accountService.getAccountsByBalanceAbove(amount);
    }

    @GetMapping("/balance-below")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> getAccountsByBalanceBelow(@RequestParam double amount) {
        return accountService.getAccountsByBalanceBelow(amount);
    }
}
