package com.mvm.transaction.repository;


import com.mvm.transaction.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserId(Long userId);

    @Query(value = "SELECT * FROM incomes i WHERE i.user_id = :userId " +
            "AND (:description IS NULL OR LOWER(i.description) LIKE LOWER(CONCAT('%', :description,'%'))) " +
            "AND (:category IS NULL OR i.category = :category) " +
            "AND (CAST(:startDate AS DATE) IS NULL OR i.date >= CAST(:startDate AS DATE)) " +
            "AND (CAST(:endDate AS DATE) IS NULL OR i.date <= CAST(:endDate AS DATE)) " +
            "AND (CAST(:minAmount AS DECIMAL) IS NULL OR i.amount >= CAST(:minAmount AS DECIMAL)) " +
            "AND (CAST(:maxAmount AS DECIMAL) IS NULL OR i.amount <= CAST(:maxAmount AS DECIMAL)) " +
            "ORDER BY i.date DESC",
            nativeQuery = true)
    List<Income> findByFiltersAndUser(
            @Param("userId") Long userId,
            @Param("description") String description,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

    @Query("SELECT DISTINCT i.userId FROM Income i WHERE i.date <= :today AND i.applicated = false")
    List<Long> findUserIdsWithPendingTransactions(@Param("today") LocalDate today);

    @Query("SELECT i FROM Income i WHERE i.userId = :userId AND i.date <= :today AND i.applicated = false")
    List<Income> findPendingByUserIdAndDate(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Modifying
    @Query(value = "DELETE FROM incomes i WHERE i.user_id = :userId " +
            "AND (:description IS NULL OR LOWER(i.description) LIKE LOWER(CONCAT('%', :description,'%'))) " +
            "AND (:category IS NULL OR i.category = :category) " +
            "AND (CAST(:startDate AS DATE) IS NULL OR i.date >= CAST(:startDate AS DATE)) " +
            "AND (CAST(:endDate AS DATE) IS NULL OR i.date <= CAST(:endDate AS DATE)) " +
            "AND (CAST(:minAmount AS DECIMAL) IS NULL OR i.amount >= CAST(:minAmount AS DECIMAL)) " +
            "AND (CAST(:maxAmount AS DECIMAL) IS NULL OR i.amount <= CAST(:maxAmount AS DECIMAL)) ",
            nativeQuery = true)
    int deleteByFiltersAndUser(
            @Param("userId") Long userId,
            @Param("description") String description,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);

}
