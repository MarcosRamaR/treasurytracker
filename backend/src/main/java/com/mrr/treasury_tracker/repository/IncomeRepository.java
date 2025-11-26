package com.mrr.treasury_tracker.repository;

import com.mrr.treasury_tracker.model.Expense;
import com.mrr.treasury_tracker.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income,Long> {
    List<Income> findByUserIdOrderByDateDesc(Long userId);

    @Query(value = "SELECT * FROM incomes e WHERE e.user_id = :userId AND " +
            "(CAST(:category AS TEXT) IS NULL OR e.category = :category) AND " +
            "(CAST(:startDate AS DATE) IS NULL OR e.date >= :startDate) AND " +
            "(CAST(:endDate AS DATE) IS NULL OR e.date <= :endDate) AND " +
            "(CAST(:minAmount AS DECIMAL) IS NULL OR e.amount >= :minAmount) AND " +
            "(CAST(:maxAmount AS DECIMAL) IS NULL OR e.amount <= :maxAmount) " +
            "ORDER BY e.date DESC",
            nativeQuery = true)
    List<Income> findByFilters(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId")
    Optional<BigDecimal> getTotalByUser(@Param("userId") Long userId);

    @Query("SELECT e.category, SUM(e.amount) FROM Income e WHERE e.user.id = :userId GROUP BY e.category ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategoryTotalsByUser(@Param("userId") Long userId);

    @Query("SELECT i FROM Income i WHERE i.user.id = :userId AND i.applicated = false AND i.date <= CURRENT_DATE")
    List<Income> findPendingIncomesToApply(@Param("userId") Long userId);

}
