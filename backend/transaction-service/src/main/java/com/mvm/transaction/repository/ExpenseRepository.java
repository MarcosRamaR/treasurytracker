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
            "AND (:category IS NULL OR e.category = :category) " +
            "AND (CAST(:startDate AS DATE) IS NULL OR e.date >= CAST(:startDate AS DATE)) " +
            "AND (CAST(:endDate AS DATE) IS NULL OR e.date <= CAST(:endDate AS DATE)) " +
            "AND (CAST(:minAmount AS DECIMAL) IS NULL OR e.amount >= CAST(:minAmount AS DECIMAL)) " +
            "AND (CAST(:maxAmount AS DECIMAL) IS NULL OR e.amount <= CAST(:maxAmount AS DECIMAL)) " +
            "ORDER BY e.date DESC",
            nativeQuery = true)
    List<Expense> findByFiltersAndUser(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);
}
