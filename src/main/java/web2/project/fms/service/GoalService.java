package web2.project.fms.service;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import web2.project.fms.exception.ResourceNotFoundException;
import web2.project.fms.mapper.RDFConverter;
import web2.project.fms.model.Account;
import web2.project.fms.model.Goal;
import web2.project.fms.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private RepositoryConnection repositoryConnection;

    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goal getGoalById(UUID id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
    }

    public Goal createGoal(Goal goal) {
        Goal savedGoal = goalRepository.save(goal);
        updateFuseki(savedGoal);
        return savedGoal;
    }

    public Goal updateGoal(UUID id, Goal goalDetails) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        goal.setName(goalDetails.getName());
        goal.setDescription(goalDetails.getDescription());
        goal.setAmount(goalDetails.getAmount());
        goal.setCategory(goalDetails.getCategory());

        Goal savedGoal = goalRepository.save(goal);
        updateFuseki(savedGoal);
        return savedGoal;
    }

    public void deleteGoal(UUID id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));
        goalRepository.delete(goal);
        removeFromFuseki(id);
    }

    public double getTotalAmountNeeded() {
        return goalRepository.findAll().stream()
                .mapToDouble(Goal::getAmount)
                .sum();
    }

    public List<Goal> getGoalsByCategory(UUID categoryId) {
        return goalRepository.findByCategory_Id(categoryId);
    }

    public List<Goal> getGoalsByAccount(UUID accountId) {
        return goalRepository.findByAccount_Id(accountId);
    }

    public boolean isGoalAchieved(UUID id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + id));

        double balance = goal.getAccount().getBalance();
        return balance >= goal.getAmount();
    }

    public double getTotalAmountNeededForCategory(UUID categoryId) {
        return goalRepository.findByCategory_Id(categoryId).stream()
                .mapToDouble(Goal::getAmount)
                .sum();
    }

    public List<Goal> getGoalsByName(String name) {
        return goalRepository.findByName(name);
    }

    public List<Goal> getGoalsWithAmountGreaterThan(double amount) {
        return goalRepository.findByAmountGreaterThan(amount);
    }

    public List<Goal> getGoalsByDescriptionKeyword(String keyword) {
        return goalRepository.findByDescriptionContaining(keyword);
    }

    public List<Goal> getGoalsByCategoryName(String categoryName) {
        return goalRepository.findByCategory_Name(categoryName);
    }

    private void updateFuseki(Goal goal) {
        Model model = RDFConverter.goalToRDF(goal);
        repositoryConnection.add(model);
    }

    private void removeFromFuseki(UUID id) {
        repositoryConnection.remove(RDFConverter.goalIRI(id), null, null);
    }
}
