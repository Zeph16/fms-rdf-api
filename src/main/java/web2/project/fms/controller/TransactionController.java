package web2.project.fms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import web2.project.fms.dto.TransactionRequest;
import web2.project.fms.mapper.TransactionMapper;
import web2.project.fms.model.Transaction;
import web2.project.fms.security.SecurityUtils;
import web2.project.fms.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest transactionReq) {
        Transaction transaction = transactionMapper.toEntity(transactionReq);
        transaction.setCreationDateTime(LocalDateTime.now());
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(createdTransaction);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable UUID id) {
        Transaction transaction = transactionService.getTransactionById(id);
        SecurityUtils.checkOwnershipOrAdmin(transaction.getAccount().getId());
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable UUID id, @RequestBody TransactionRequest transactionReq) {
        Transaction transaction = transactionMapper.toEntity(transactionReq);
        SecurityUtils.checkOwnershipOrAdmin(transaction.getAccount().getId());
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        Transaction existingTransaction = transactionService.getTransactionById(id);
        SecurityUtils.checkOwnershipOrAdmin(existingTransaction.getAccount().getId());
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Transaction> getAllTransactions() {
        UUID accountId = SecurityUtils.getAuthenticatedAccountId();
        return transactionService.getTransactionsByAccount(accountId);
    }

    @GetMapping("/by-category/{categoryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Transaction> getTransactionsByCategory(@PathVariable UUID categoryId) {
        List<Transaction> transactions = transactionService.getTransactionsByCategory(categoryId);
        return transactions.stream()
                .filter(transaction -> SecurityUtils.isAdmin() || transaction.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/within-date-range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Transaction> getTransactionsWithinDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.getTransactionsWithinDateRange(startDate, endDate);
        return transactions.stream()
                .filter(transaction -> SecurityUtils.isAdmin() || transaction.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/after-date")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Transaction> getTransactionsAfterDate(@RequestParam LocalDateTime date) {
        List<Transaction> transactions = transactionService.getTransactionsAfterDate(date);
        return transactions.stream()
                .filter(transaction -> SecurityUtils.isAdmin() || transaction.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/before-date")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Transaction> getTransactionsBeforeDate(@RequestParam LocalDateTime date) {
        List<Transaction> transactions = transactionService.getTransactionsBeforeDate(date);
        return transactions.stream()
                .filter(transaction -> SecurityUtils.isAdmin() || transaction.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/in-month")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Transaction> getTransactionsInMonth(@RequestParam int year, @RequestParam int month) {
        List<Transaction> transactions = transactionService.getTransactionsInMonth(year, month);
        return transactions.stream()
                .filter(transaction -> SecurityUtils.isAdmin() || transaction.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/in-year")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Transaction> getTransactionsInYear(@RequestParam int year) {
        List<Transaction> transactions = transactionService.getTransactionsInYear(year);
        return transactions.stream()
                .filter(transaction -> SecurityUtils.isAdmin() || transaction.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/total-amount/{categoryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalAmountByCategoryId(@PathVariable UUID categoryId) {
        double totalAmount = transactionService.getTotalAmountByCategoryId(categoryId);
        return ResponseEntity.ok(totalAmount);
    }

}
