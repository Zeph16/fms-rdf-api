package web2.project.fms.mapper;

import web2.project.fms.dto.TransactionRequest;
import web2.project.fms.model.Transaction;
import web2.project.fms.security.SecurityUtils;
import web2.project.fms.service.AccountService;
import web2.project.fms.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    public Transaction toEntity(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setType(transactionRequest.getType());
        transaction.setAccount(accountService.getAccountById(SecurityUtils.getAuthenticatedAccountId()));
        transaction.setCategory(categoryService.getCategoryById(transactionRequest.getCategoryId()));
        return transaction;
    }

    public TransactionRequest toDto(Transaction transaction) {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(transaction.getAmount());
        transactionRequest.setDescription(transaction.getDescription());
        transactionRequest.setCategoryId(transaction.getCategory().getId());
        transactionRequest.setType(transaction.getType());
        return transactionRequest;
    }
}
