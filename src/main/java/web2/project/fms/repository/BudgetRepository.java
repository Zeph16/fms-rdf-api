package web2.project.fms.repository;

import org.springframework.stereotype.Repository;
import web2.project.fms.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    List<Budget> findByCategory_Id(UUID categoryId);
    List<Budget> findByAccount_Id(UUID accountId);
    List<Budget> findByName(String name);
    List<Budget> findByAmountGreaterThan(double amount);
    List<Budget> findByCategory_Name(String categoryName);
}