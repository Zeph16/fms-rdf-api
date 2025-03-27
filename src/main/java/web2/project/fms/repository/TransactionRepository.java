package web2.project.fms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web2.project.fms.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByAccount_Id(UUID accountId);

    List<Transaction> findByCategory_Id(UUID categoryId);

    List<Transaction> findByCreationDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByCreationDateTimeAfter(LocalDateTime date);

    List<Transaction> findByCreationDateTimeBefore(LocalDateTime date);

    @Query("SELECT t FROM Transaction t WHERE YEAR(t.creationDateTime) = :year AND MONTH(t.creationDateTime) = :month")
    List<Transaction> findByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT t FROM Transaction t WHERE YEAR(t.creationDateTime) = :year")
    List<Transaction> findByYear(@Param("year") int year);
}