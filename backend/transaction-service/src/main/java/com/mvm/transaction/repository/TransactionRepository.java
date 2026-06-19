package com.mvm.transaction.repository;

import com.mvm.transaction.model.Transaction;
import com.mvm.transaction.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    List<Transaction> findAllByUserId(Long userId);

    Page<Transaction> findByUserIdAndType(Long userId, TransactionType type, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.date <= :today AND t.applicated = false")
    List<Transaction> findPendingByUserIdAndDate(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT DISTINCT t.userId FROM Transaction t WHERE t.date <= :today AND t.applicated = false")
    List<Long> findUserIdsWithPendingTransactions(@Param("today") LocalDate today);

    @Modifying
    @Query("UPDATE Transaction t SET t.applicated = true WHERE t.userId = :userId AND t.id IN :ids")
    int markAsApplicated(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.type = :type")
    long countByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.type = :type AND t.applicated = true")
    BigDecimal sumApplicatedByUserIdAndType(@Param("userId") Long userId, @Param("type") TransactionType type);
}