package web2.project.fms.repository;

import org.springframework.stereotype.Repository;
import web2.project.fms.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByAccount_Id(UUID accountId);
    List<Category> findByName(String name);
    List<Category> findByDescriptionContaining(String keyword);
    long countByAccount_Id(UUID accountId);
}
