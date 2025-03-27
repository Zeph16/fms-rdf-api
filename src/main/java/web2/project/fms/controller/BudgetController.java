package web2.project.fms.controller;

import web2.project.fms.dto.BudgetRequest;
import web2.project.fms.mapper.BudgetMapper;
import web2.project.fms.model.Budget;
import web2.project.fms.service.BudgetService;
import web2.project.fms.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetMapper budgetMapper;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Budget> getAllBudgets() {
        if (SecurityUtils.isAdmin()) {
            return budgetService.getAllBudgets();
        }
        return budgetService.getBudgetsByAccount(SecurityUtils.getAuthenticatedAccountId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Budget> getBudgetById(@PathVariable UUID id) {
        Budget budget = budgetService.getBudgetById(id);
        SecurityUtils.checkOwnershipOrAdmin(budget.getAccount().getId());
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Budget> createBudget(@RequestBody BudgetRequest budgetDetails) {
        Budget createdBudget = budgetService.createBudget(budgetMapper.toEntity(budgetDetails));
        return ResponseEntity.ok(createdBudget);
    }

    // Update a budget
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Budget> updateBudget(@PathVariable UUID id, @RequestBody BudgetRequest budgetDetails) {
        Budget existingBudget = budgetService.getBudgetById(id);
        SecurityUtils.checkOwnershipOrAdmin(existingBudget.getAccount().getId());
        Budget updatedBudget = budgetService.updateBudget(id, budgetMapper.toEntity(budgetDetails));
        return ResponseEntity.ok(updatedBudget);
    }

    // Delete a budget
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBudget(@PathVariable UUID id) {
        Budget budget = budgetService.getBudgetById(id);
        SecurityUtils.checkOwnershipOrAdmin(budget.getAccount().getId());
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    // Get total budget amount
    @GetMapping("/total-amount/")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public double getTotalBudgetAmount() {
        if (SecurityUtils.isAdmin()) {
            return budgetService.getAllBudgets().stream().mapToDouble(Budget::getAmount).sum();
        }
        return budgetService.getTotalBudgetAmount(SecurityUtils.getAuthenticatedAccountId());
    }

    // Get budgets by name
    @GetMapping("/by-name/{name}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Budget> getBudgetsByName(@PathVariable String name) {
        if (SecurityUtils.isAdmin()) {
            return budgetService.getBudgetsByName(name);
        }

        return budgetService.getBudgetsByName(name).stream()
                .filter(budget -> budget.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/amount-greater-than/{amount}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Budget> getBudgetsWithAmountGreaterThan(@PathVariable double amount) {
        if (SecurityUtils.isAdmin()) {
            return budgetService.getBudgetsWithAmountGreaterThan(amount);
        }
        return budgetService.getBudgetsWithAmountGreaterThan(amount).stream()
                .filter(budget -> budget.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/by-category/{categoryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Budget> getBudgetsByCategory(@PathVariable UUID categoryId) {
        if (SecurityUtils.isAdmin()) {
            return budgetService.getBudgetsByCategory(categoryId);
        }
        return budgetService.getBudgetsByCategory(categoryId).stream()
                .filter(budget -> budget.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }

    @GetMapping("/exceeded")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Budget> getExceededBudgets() {
        if (SecurityUtils.isAdmin()) {
            return budgetService.getExceededBudgets();
        }
        return budgetService.getExceededBudgets().stream()
                .filter(budget -> budget.getAccount().getId().equals(SecurityUtils.getAuthenticatedAccountId()))
                .collect(Collectors.toList());
    }
}
