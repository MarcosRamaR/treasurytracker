package com.mvm.transaction.service;

import com.mvm.transaction.dto.BalanceDTO;
import com.mvm.transaction.exception.BalanceNotFoundException;
import com.mvm.transaction.mapper.BalanceMapper;
import com.mvm.transaction.model.Balance;
import com.mvm.transaction.model.Transaction;
import com.mvm.transaction.model.TransactionType;
import com.mvm.transaction.repository.BalanceRepository;
import com.mvm.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceMapper balanceMapper;

    public BalanceDTO createInitialBalanceForUser(Long userId) {
        return balanceRepository.findByUserId(userId)
                .map(balanceMapper::toDTO)
                .orElseGet(() -> balanceMapper.toDTO(balanceRepository.save(new Balance(userId))));
    }

    @Transactional
    public BalanceDTO updateBalanceAutomatically(Long userId) {
        log.debug("Updating balance automatically for user: {}", userId);

        LocalDate today = LocalDate.now();

        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> balanceRepository.save(new Balance(userId)));

        BigDecimal originalBalance = balance.getTotalBalance();

        List<Transaction> pendingTransactions = transactionRepository.findPendingByUserIdAndDate(userId, today);

        BigDecimal totalExpensesPending = BigDecimal.ZERO;
        BigDecimal totalIncomesPending = BigDecimal.ZERO;

        for (Transaction t : pendingTransactions) {
            if (t.getType() == TransactionType.EXPENSE) {
                totalExpensesPending = totalExpensesPending.add(t.getAmount());
            } else {
                totalIncomesPending = totalIncomesPending.add(t.getAmount());
            }
            t.setApplicated(true);
        }

        if (!pendingTransactions.isEmpty()) {
            transactionRepository.saveAll(pendingTransactions);

            BigDecimal newBalance = originalBalance
                    .subtract(totalExpensesPending)
                    .add(totalIncomesPending);

            balance.setTotalBalance(newBalance);
            balance.setModifiedBy("SYSTEM");

            Balance saved = balanceRepository.save(balance);
            log.debug("Balance updated for user {}: {} -> {} (expenses: {}, incomes: {})",
                    userId, originalBalance, newBalance, totalExpensesPending, totalIncomesPending);
            return balanceMapper.toDTO(saved);
        }

        log.debug("No pending transactions for user: {}", userId);
        return balanceMapper.toDTO(balance);
    }

    @Transactional
    public BalanceDTO updateBalanceFromNewExpense(Long userId, BigDecimal expenseAmount) {
        return adjustBalance(userId, expenseAmount.negate(), "SYSTEM");
    }

    @Transactional
    public BalanceDTO updateBalanceFromNewIncome(Long userId, BigDecimal incomeAmount) {
        return adjustBalance(userId, incomeAmount, "SYSTEM");
    }

    @Transactional
    public BalanceDTO revertExpense(Long userId, BigDecimal expenseAmount) {
        return adjustBalance(userId, expenseAmount, "REVERT_EXPENSE");
    }

    @Transactional
    public BalanceDTO revertIncome(Long userId, BigDecimal incomeAmount) {
        return adjustBalance(userId, incomeAmount.negate(), "REVERT_INCOME");
    }

    private BalanceDTO adjustBalance(Long userId, BigDecimal delta, String modifiedBy) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> balanceRepository.save(new Balance(userId)));

        BigDecimal newBalance = balance.getTotalBalance().add(delta);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Balance would go negative for user {}: {} + {} = {}. Allowing but logging.",
                    userId, balance.getTotalBalance(), delta, newBalance);
        }

        balance.setTotalBalance(newBalance);
        balance.setModifiedBy(modifiedBy);

        return balanceMapper.toDTO(balanceRepository.save(balance));
    }

    @Scheduled(cron = "0 1 0,12 * * ?")
    @Async("balanceTaskExecutor")
    public CompletableFuture<Void> applyAllPendingTransactionsAsync() {
        log.info("Scheduled: applying all pending transactions");
        LocalDate today = LocalDate.now();

        List<Long> userIdsWithPending = transactionRepository.findUserIdsWithPendingTransactions(today);

        log.info("Found {} users with pending transactions", userIdsWithPending.size());

        for (Long userId : userIdsWithPending) {
            try {
                updateBalanceAutomatically(userId);
            } catch (Exception e) {
                log.error("Error updating balance for user {}: {}", userId, e.getMessage(), e);
            }
        }

        log.info("Scheduled task completed");
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    public BalanceDTO updateBalanceManual(Long userId, BigDecimal amount) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> balanceRepository.save(new Balance(userId)));

        balance.setTotalBalance(amount);
        balance.setModifiedBy("MANUAL");

        return balanceMapper.toDTO(balanceRepository.save(balance));
    }

    public BalanceDTO getBalanceByUserId(Long userId) {
        return balanceRepository.findByUserId(userId)
                .map(balanceMapper::toDTO)
                .orElseThrow(() -> new BalanceNotFoundException(userId));
    }

    public BigDecimal getTotalBalance(Long userId) {
        return balanceRepository.findByUserId(userId)
                .map(Balance::getTotalBalance)
                .orElse(BigDecimal.ZERO);
    }
}