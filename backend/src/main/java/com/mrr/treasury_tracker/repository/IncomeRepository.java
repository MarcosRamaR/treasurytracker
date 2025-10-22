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

@Repository
public interface IncomeRepository extends JpaRepository<Income,Long> {
    @Query(value = "SELECT * FROM incomes e WHERE " +
            "(CAST(:category AS TEXT) IS NULL OR e.category = :category) AND " +
            "(CAST(:startDate AS DATE) IS NULL OR e.date >= :startDate) AND " +
            "(CAST(:endDate AS DATE) IS NULL OR e.date <= :endDate) AND " +
            "(CAST(:minAmount AS DECIMAL) IS NULL OR e.amount >= :minAmount) AND " +
            "(CAST(:maxAmount AS DECIMAL) IS NULL OR e.amount <= :maxAmount) " +
            "ORDER BY e.date DESC",
            nativeQuery = true)
    List<Income> findByFilters(
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

}
