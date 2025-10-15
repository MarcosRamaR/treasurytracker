package com.mrr.treasury_tracker.repository;


import com.mrr.treasury_tracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository //Sets this interface as spring component to access data
//JpaRepository give basic CRUD, expense is the entity type and long the PK type
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategoryOrderByDateDesc (String category);
    List<Expense> findByDateBetweenOrderByDateDesc (LocalDate startDate, LocalDate endDate);
    List<Expense> findByAmountGreaterThanOrderByDateDesc(BigDecimal amount);
    List<Expense> findByCategoryAndDateBetween(String category,LocalDate startDate, LocalDate endDate);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryTotals(); //With object this array can contain any type, each object have category + total

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    //Optional to not return null if not expense on that range
    Optional<BigDecimal> getTotalByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = YEAR(CURRENT_DATE) AND " +
            "MONTH(e.date) = MONTH(CURRENT_DATE) ORDER BY e.date DESC")
    List<Expense> findCurrentMonthExpenses(); //Find all expenses of actual month

}
