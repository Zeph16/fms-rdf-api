package web2.project.fms.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web2.project.fms.exception.ResourceNotFoundException;
import web2.project.fms.mapper.RDFConverter;
import web2.project.fms.model.Account;
import web2.project.fms.repository.AccountRepository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    private RepositoryConnection repositoryConnection;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
    }

    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with username: " + username));
    }

    public Account createAccount(Account account) {
        Account savedAccount = accountRepository.save(account);
        updateFuseki(savedAccount);
        return savedAccount;
    }

    public Account updateAccount(UUID id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        if (account != null) {
            account.setName(accountDetails.getName());
            account.setUsername(accountDetails.getUsername());
            account.setPassword(accountDetails.getPassword());
            Account savedAccount = accountRepository.save(account);
            updateFuseki(savedAccount);
            return savedAccount;
        } else {
            return null;
        }
    }

    public void deleteAccount(UUID id) {
        accountRepository.deleteById(id);
        removeFromFuseki(id);
    }

    public List<Account> findAccountsByName(String name) {
        return accountRepository.findByNameContainingIgnoreCase(name);
    }


    public List<Account> getAccountsByBalanceAbove(double amount) {
        return accountRepository.findByBalanceGreaterThan(amount);
    }

    public List<Account> getAccountsByBalanceBelow(double amount) {
        return accountRepository.findByBalanceLessThan(amount);
    }


    void updateFuseki(Account account) {
        Model model = RDFConverter.accountToRDF(account);
        repositoryConnection.add(model);
    }

    private void removeFromFuseki(UUID id) {
        repositoryConnection.remove(RDFConverter.accountIRI(id), null, null);
    }
}
