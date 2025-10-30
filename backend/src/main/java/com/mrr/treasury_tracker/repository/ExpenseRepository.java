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
    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user.id = :userId GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryTotalsByUser(@Param("userId") Long userId); //With object this array can contain any type, each object have category + total

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :startDate AND :endDate")
    //Optional to not return null if not expense on that range
    Optional<BigDecimal> getTotalByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND YEAR(e.date) = YEAR(CURRENT_DATE) AND " +
            "MONTH(e.date) = MONTH(CURRENT_DATE) ORDER BY e.date DESC")
    List<Expense> findCurrentMonthExpenses(@Param("userId") Long userId); //Find all expenses of actual month

    @Query(value = "SELECT * FROM expenses e WHERE e.user_id = :userId AND" +
            "(CAST(:category AS TEXT) IS NULL OR e.category = :category) AND " +
            "(CAST(:startDate AS DATE) IS NULL OR e.date >= :startDate) AND " +
            "(CAST(:endDate AS DATE) IS NULL OR e.date <= :endDate) AND " +
            "(CAST(:minAmount AS DECIMAL) IS NULL OR e.amount >= :minAmount) AND " +
            "(CAST(:maxAmount AS DECIMAL) IS NULL OR e.amount <= :maxAmount) " +
            "ORDER BY e.date DESC",
            nativeQuery = true)
    List<Expense> findByFiltersAndUser(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Optional<BigDecimal> getTotalByUser(@Param("userId") Long userId);


}
