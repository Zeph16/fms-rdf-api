package web2.project.fms.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web2.project.fms.exception.ResourceNotFoundException;
import web2.project.fms.mapper.RDFConverter;
import web2.project.fms.model.Account;
import web2.project.fms.model.Transaction;
import web2.project.fms.model.TransactionType;
import web2.project.fms.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final RepositoryConnection repositoryConnection;

    public Transaction createTransaction(Transaction transaction) {
        transaction.setCreationDateTime(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        updateAccountBalance(savedTransaction, true);
        updateFuseki(savedTransaction);
        return savedTransaction;
    }

    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    public Transaction updateTransaction(UUID transactionId, Transaction updatedTransaction) {
        Transaction existingTransaction = getTransactionById(transactionId);
        updateAccountBalance(existingTransaction, false); // Revert the balance update for the existing transaction
        updatedTransaction.setId(transactionId);
        updatedTransaction.setCreationDateTime(existingTransaction.getCreationDateTime());
        Transaction savedTransaction = transactionRepository.save(updatedTransaction);
        updateAccountBalance(savedTransaction, true); // Apply the balance update for the new transaction
        updateFuseki(savedTransaction);
        return savedTransaction;
    }

    public void deleteTransaction(UUID transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        updateAccountBalance(transaction, false); // Revert the balance update for the existing transaction
        transactionRepository.delete(transaction);
        removeFromFuseki(transactionId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByAccount(UUID accountId) {
        return transactionRepository.findByAccount_Id(accountId);
    }

    public List<Transaction> getTransactionsByCategory(UUID categoryId) {
        return transactionRepository.findByCategory_Id(categoryId);
    }
    public List<Transaction> getTransactionsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByCreationDateTimeBetween(startDate, endDate);
    }

    public List<Transaction> getTransactionsAfterDate(LocalDateTime date) {
        return transactionRepository.findByCreationDateTimeAfter(date);
    }

    public List<Transaction> getTransactionsBeforeDate(LocalDateTime date) {
        return transactionRepository.findByCreationDateTimeBefore(date);
    }

    public List<Transaction> getTransactionsInMonth(int year, int month) {
        return transactionRepository.findByMonth(year, month);
    }

    public List<Transaction> getTransactionsInYear(int year) {
        return transactionRepository.findByYear(year);
    }


    @Transactional
    public void transferFunds(UUID fromAccountId, UUID toAccountId, double amount) {
        Account fromAccount = accountService.getAccountById(fromAccountId);
        Account toAccount = accountService.getAccountById(toAccountId);

        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in the source account");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        accountService.updateAccount(fromAccountId, fromAccount);
        accountService.updateFuseki(fromAccount);

        toAccount.setBalance(toAccount.getBalance() + amount);
        accountService.updateAccount(toAccountId, toAccount);
        accountService.updateFuseki(toAccount);

        Transaction fromTransaction = new Transaction();
        fromTransaction.setAmount(amount);
        fromTransaction.setDescription("Transfer to " + toAccount.getName());
        fromTransaction.setType(TransactionType.EXPENSE);
        fromTransaction.setAccount(fromAccount);
        createTransaction(fromTransaction);

        Transaction toTransaction = new Transaction();
        toTransaction.setAmount(amount);
        toTransaction.setDescription("Transfer from " + fromAccount.getName());
        toTransaction.setType(TransactionType.INCOME);
        toTransaction.setAccount(toAccount);
        createTransaction(toTransaction);

        updateFuseki(fromTransaction);
        updateFuseki(toTransaction);
    }
    private void updateAccountBalance(Transaction transaction, boolean isAdd) {
        Account account = transaction.getAccount();
        double amount = transaction.getAmount();
        if (transaction.getType() == TransactionType.EXPENSE) {
            amount = -amount;
        }
        account.setBalance(account.getBalance() + (isAdd ? amount : -amount));
        accountService.updateAccount(account.getId(), account);
        accountService.updateFuseki(account);
    }

    public double getTotalAmountByCategoryId(UUID categoryId) {
        List<Transaction> transactions = transactionRepository.findByCategory_Id(categoryId);
        double totalAmount = 0.0;
        for (Transaction transaction : transactions) {
            totalAmount += transaction.getAmount();
        }
        return totalAmount;
    }

    private void updateFuseki(Transaction transaction) {
        Model model = RDFConverter.transactionToRDF(transaction);
        repositoryConnection.add(model);
    }

    private void removeFromFuseki(UUID id) {
        repositoryConnection.remove(RDFConverter.transactionIRI(id), null, null);
    }
}
