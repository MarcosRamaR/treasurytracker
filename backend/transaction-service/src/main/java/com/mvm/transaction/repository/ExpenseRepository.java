package com.mvm.transaction.repository;

import com.mvm.transaction.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);

    @Query(value = "SELECT * FROM expenses e WHERE e.user_id = :userId " +
            /*LOWER(e.description) convert description on DB to lower case,
            LIKE look for partial matches
            LOWER(CONCAT('%', :description, '%')) convert the search param on lower case and % to search any description contains that*/
            "AND (:description IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description,'%'))) " +
            "AND (:category IS NULL OR e.category = :category) " +
            "AND (CAST(:startDate AS DATE) IS NULL OR e.date >= CAST(:startDate AS DATE)) " +
            "AND (CAST(:endDate AS DATE) IS NULL OR e.date <= CAST(:endDate AS DATE)) " +
            "AND (CAST(:minAmount AS DECIMAL) IS NULL OR e.amount >= CAST(:minAmount AS DECIMAL)) " +
            "AND (CAST(:maxAmount AS DECIMAL) IS NULL OR e.amount <= CAST(:maxAmount AS DECIMAL)) " +
            "ORDER BY e.date DESC",
            nativeQuery = true)
    List<Expense> findByFiltersAndUser(
            @Param("userId") Long userId,
            @Param("description") String description,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT DISTINCT e.userId FROM Expense e WHERE e.date <= :today AND e.applicated = false")
    List<Long> findUserIdsWithPendingTransactions(@Param("today") LocalDate today);

    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.date <= :today AND e.applicated = false")
    List<Expense> findPendingByUserIdAndDate(@Param("userId") Long userId, @Param("today") LocalDate today);
}
