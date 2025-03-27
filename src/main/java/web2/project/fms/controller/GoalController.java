package web2.project.fms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import web2.project.fms.dto.GoalRequest;
import web2.project.fms.mapper.GoalMapper;
import web2.project.fms.model.Goal;
import web2.project.fms.service.GoalService;
import web2.project.fms.security.SecurityUtils;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private GoalMapper goalMapper;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Goal> getAllGoals() {
        if (SecurityUtils.isAdmin()) {
            return goalService.getAllGoals();
        }
        return goalService.getGoalsByAccount(SecurityUtils.getAuthenticatedAccountId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Goal> getGoalById(@PathVariable UUID id) {
        Goal goal = goalService.getGoalById(id);
        SecurityUtils.checkOwnershipOrAdmin(goal.getAccount().getId());
        return ResponseEntity.ok(goal);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Goal> createGoal(@RequestBody GoalRequest goalReq) {
        Goal createdGoal = goalService.createGoal(goalMapper.toEntity(goalReq));
        return ResponseEntity.ok(createdGoal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Goal> updateGoal(@PathVariable UUID id, @RequestBody GoalRequest goalReq) {
        Goal existingGoal = goalService.getGoalById(id);
        SecurityUtils.checkOwnershipOrAdmin(existingGoal.getAccount().getId());
        Goal updatedGoal = goalService.updateGoal(id, goalMapper.toEntity(goalReq));
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGoal(@PathVariable UUID id) {
        Goal goal = goalService.getGoalById(id);
        SecurityUtils.checkOwnershipOrAdmin(goal.getAccount().getId());
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/achieved")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> isGoalAchieved(@PathVariable UUID id) {
        boolean achieved = goalService.isGoalAchieved(id);
        return ResponseEntity.ok(achieved);
    }

    @GetMapping("/total-amount/{categoryId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Double> getTotalAmountNeededForCategory(@PathVariable UUID categoryId) {
        double totalAmount = goalService.getTotalAmountNeededForCategory(categoryId);
        return ResponseEntity.ok(totalAmount);
    }
}
