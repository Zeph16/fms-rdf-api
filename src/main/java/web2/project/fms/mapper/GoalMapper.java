package web2.project.fms.mapper;

import web2.project.fms.dto.GoalRequest;
import web2.project.fms.model.Goal;
import web2.project.fms.security.SecurityUtils;
import web2.project.fms.service.AccountService;
import web2.project.fms.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    public Goal toEntity(GoalRequest goalRequest) {
        Goal goal = new Goal();
        goal.setName(goalRequest.getName());
        goal.setDescription(goalRequest.getDescription());
        goal.setAmount(goalRequest.getAmount());
        goal.setAccount(accountService.getAccountById(SecurityUtils.getAuthenticatedAccountId()));
        goal.setCategory(categoryService.getCategoryById(goalRequest.getCategoryId()));
        return goal;
    }

    public GoalRequest toDto(Goal goal) {
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setName(goal.getName());
        goalRequest.setDescription(goal.getDescription());
        goalRequest.setAmount(goal.getAmount());
        goalRequest.setCategoryId(goal.getCategory().getId());
        return goalRequest;
    }
}
