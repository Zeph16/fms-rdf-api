package web2.project.fms.mapper;

import web2.project.fms.dto.BudgetRequest;
import web2.project.fms.model.Budget;
import web2.project.fms.security.SecurityUtils;
import web2.project.fms.service.AccountService;
import web2.project.fms.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    public Budget toEntity(BudgetRequest budgetRequest) {
        Budget budget = new Budget();
        budget.setName(budgetRequest.getName());
        budget.setAmount(budgetRequest.getAmount());
        budget.setAccount(accountService.getAccountById(SecurityUtils.getAuthenticatedAccountId()));
        budget.setCategory(categoryService.getCategoryById(budgetRequest.getCategoryId()));
        return budget;
    }

    public BudgetRequest toDto(Budget budget) {
        BudgetRequest budgetRequest = new BudgetRequest();
        budgetRequest.setName(budget.getName());
        budgetRequest.setAmount(budget.getAmount());
        budgetRequest.setCategoryId(budget.getCategory().getId());
        return budgetRequest;
    }
}
