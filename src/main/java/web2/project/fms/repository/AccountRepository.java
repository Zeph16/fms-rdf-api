package web2.project.fms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web2.project.fms.model.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUsername(String username);
    List<Account> findByNameContainingIgnoreCase(String name);
    List<Account> findByBalanceGreaterThan(double amount);
    List<Account> findByBalanceLessThan(double amount);
}