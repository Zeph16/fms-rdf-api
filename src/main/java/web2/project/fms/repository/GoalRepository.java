package web2.project.fms.repository;

import org.springframework.stereotype.Repository;
import web2.project.fms.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByCategory_Id(UUID categoryId);
    List<Goal> findByAccount_Id(UUID accountId);
    List<Goal> findByName(String name);
    List<Goal> findByAmountGreaterThan(double amount);
    List<Goal> findByDescriptionContaining(String keyword);
    List<Goal> findByCategory_Name(String categoryName);
}