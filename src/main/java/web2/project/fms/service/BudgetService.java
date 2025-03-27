package web2.project.fms.service;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import web2.project.fms.exception.ResourceNotFoundException;
import web2.project.fms.mapper.RDFConverter;
import web2.project.fms.model.Account;
import web2.project.fms.model.Budget;
import web2.project.fms.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RepositoryConnection repositoryConnection;

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(UUID id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
    }

    public Budget createBudget(Budget budget) {
        Budget savedBudget = budgetRepository.save(budget);
        updateFuseki(savedBudget);
        return savedBudget;
    }

    public Budget updateBudget(UUID id, Budget budgetDetails) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        budget.setName(budgetDetails.getName());
        budget.setAmount(budgetDetails.getAmount());
        budget.setCategory(budgetDetails.getCategory());

        Budget savedBudget = budgetRepository.save(budget);
        updateFuseki(savedBudget);
        return savedBudget;
    }

    public void deleteBudget(UUID id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        budgetRepository.delete(budget);
        removeFromFuseki(id);
    }

    public double getTotalBudgetAmount(UUID accountId) {
        return getBudgetsByAccount(accountId).stream()
                .mapToDouble(Budget::getAmount)
                .sum();
    }

    public List<Budget> getBudgetsByCategory(UUID categoryId) {
        return budgetRepository.findByCategory_Id(categoryId);
    }

    public List<Budget> getBudgetsByAccount(UUID accountId) {
        return budgetRepository.findByAccount_Id(accountId);
    }

    public List<Budget> getBudgetsByName(String name) {
        return budgetRepository.findByName(name);
    }

    public List<Budget> getBudgetsWithAmountGreaterThan(double amount) {
        return budgetRepository.findByAmountGreaterThan(amount);
    }

    public List<Budget> getBudgetsByCategoryName(String categoryName) {
        return budgetRepository.findByCategory_Name(categoryName);
    }

    public List<Budget> getExceededBudgets() {
        List<Budget> budgets = budgetRepository.findAll();

        return budgets.stream()
                .filter(budget -> transactionService.getTotalAmountByCategoryId(budget.getCategory().getId()) > budget.getAmount())
                .collect(Collectors.toList());
    }

    private void updateFuseki(Budget budget) {
        Model model = RDFConverter.budgetToRDF(budget);
        repositoryConnection.add(model);
    }

    private void removeFromFuseki(UUID id) {
        repositoryConnection.remove(RDFConverter.budgetIRI(id), null, null);
    }
}
