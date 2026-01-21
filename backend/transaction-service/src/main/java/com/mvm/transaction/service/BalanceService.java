package com.mvm.transaction.service;

import com.mvm.transaction.dto.BalanceDTO;
import com.mvm.transaction.model.Balance;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.model.Income;
import com.mvm.transaction.repository.BalanceRepository;
import com.mvm.transaction.repository.ExpenseRepository;
import com.mvm.transaction.repository.IncomeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private IncomeRepository incomeRepository;

    public BalanceService() {
        log.info("=== Created balance instance ===");
        log.info("Scheduled task configured");
    }

    public Balance createInitialBalance(Long userId) {
        Balance balance = new Balance();
        balance.setUserId(userId);
        balance.setTotalBalance(BigDecimal.ZERO);
        balance.setModifiedBy("SYSTEM");
        return balanceRepository.save(balance);
    }

    //Method called by controller when auth-service requests the creation of an initial balance
    public BalanceDTO createInitialBalanceForUser(Long userId) {
        //First check if exists a balance for this user
        //var type is deduced by compiler, is equivalen to Optional<Balance> existingBalance (Java 10+)
        var existingBalance = balanceRepository.findByUserId(userId);
        if (existingBalance.isPresent()) {
            //If exists, we return the existent one
            return convertToDTO(existingBalance.get());
        }
        Balance balance = createInitialBalance(userId);
        return convertToDTO(balance);
    }

    public BalanceDTO updateBalanceAutomatically(Long userId) {
        log.info("=== Update balance automatically ===");
        log.info("For user ID: {}", userId);

        LocalDate today = LocalDate.now();

        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> createInitialBalance(userId));
        BigDecimal originalBalance = balance.getTotalBalance();

        //Calculate import of expenses and incomes pending
        List<Expense> pendingExpenses = expenseRepository.findPendingByUserIdAndDate(userId, today);
        BigDecimal totalExpensesPending = applyPendingExpenses(pendingExpenses);

        List<Income> pendingIncomes = incomeRepository.findPendingByUserIdAndDate(userId, today);
        BigDecimal totalIncomesPending = applyPendingIncomes(pendingIncomes);

        //BigDecimal cant be compared direct with > 0
        if(totalExpensesPending.compareTo(BigDecimal.ZERO)> 0 || totalIncomesPending.compareTo(BigDecimal.ZERO) > 0){
            BigDecimal newBalance = originalBalance.subtract(totalExpensesPending).add(totalIncomesPending);
            balance.setTotalBalance(newBalance);
            balance.setModifiedBy("SYSTEM");
            balance.setUpdatedAt(LocalDateTime.now());

            Balance savedBalance = balanceRepository.save(balance);
            return convertToDTO(savedBalance);
        }else{
            log.info("Not pending transactions");
            return convertToDTO(balance);
        }
    }
    public BalanceDTO updateBalanceFromNewExpense(Long userId, BigDecimal expenseAmount){
        Balance balance = balanceRepository.findByUserId(userId).orElseGet(() -> createInitialBalance(userId));

        BigDecimal originalBalance = balance.getTotalBalance();
        BigDecimal newBalance = originalBalance.subtract(expenseAmount);

        balance.setTotalBalance(newBalance);
        balance.setModifiedBy("SYSTEM");
        balance.setUpdatedAt(LocalDateTime.now());
        Balance savedBalance = balanceRepository.save(balance);
        return convertToDTO(savedBalance);
    }
    public BalanceDTO updateBalanceFromNewIncome(Long userId, BigDecimal incomeAmount){
        Balance balance = balanceRepository.findByUserId(userId).orElseGet(() -> createInitialBalance(userId));

        BigDecimal originalBalance = balance.getTotalBalance();
        BigDecimal newBalance = originalBalance.add(incomeAmount);

        balance.setTotalBalance(newBalance);
        balance.setModifiedBy("SYSTEM");
        balance.setUpdatedAt(LocalDateTime.now());
        Balance savedBalance = balanceRepository.save(balance);
        return convertToDTO(savedBalance);
    }

    @Scheduled(cron = "0 0 0,12 * * ?") //Sec 0, min 0, hour 0 and hour 12. 2 times at day (00:00 and 12:00)
    @Transactional //if throw an error in middle of processing rollback information
    public void applyAllPendingTransactions() {
        log.info("=== Scheduled: applying all pending transactions ===");
        log.info("Date {}", LocalDate.now());
        LocalDate today = LocalDate.now();
        //Get user with pending expenses or incomes
        List<Long> userIdsWithPending = getAllUserIdsWithPendingTransactions(today);

        for (Long userId : userIdsWithPending) {
            try {
                updateBalanceAutomatically(userId);
            } catch (Exception e) {
                log.error("Error for user {}: {}", userId, e.getMessage());
            }
        }
        log.info("===Scheduled task completed ===");
    }

    private List<Long> getAllUserIdsWithPendingTransactions(LocalDate today) {
        List<Long> usersWithPendingExpenses = expenseRepository.findUserIdsWithPendingTransactions(today);
        List<Long> usersWithPendingIncomes = incomeRepository.findUserIdsWithPendingTransactions(today);

        usersWithPendingExpenses.addAll(usersWithPendingIncomes);
        return usersWithPendingExpenses.stream().distinct().collect(Collectors.toList());
    }


    public BalanceDTO updateBalanceManual(Long userId, BigDecimal amount) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> createInitialBalance(userId));
        log.debug("Amount on Service: {}", amount);
        balance.setTotalBalance(amount);
        balance.setModifiedBy("MANUAL");
        balance.setUpdatedAt(LocalDateTime.now());
        Balance savedBalance = balanceRepository.save(balance);

        return convertToDTO(savedBalance);
    }

    //Search the users balance or creates one if don't exist
    public BalanceDTO getBalanceByUserId(Long userId) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> createInitialBalance(userId));
        return convertToDTO(balance);
    }

    private BalanceDTO convertToDTO(Balance balance) {
        BalanceDTO dto = new BalanceDTO();
        dto.setId(balance.getId());
        dto.setUserId(balance.getUserId());
        dto.setTotalBalance(balance.getTotalBalance());
        return dto;
    }

    //Calculated pending expenses and set them as applied
    private BigDecimal applyPendingExpenses(List<Expense> pendingExpenses) {
        BigDecimal total = BigDecimal.ZERO;

        for (Expense expense : pendingExpenses) {
            total = total.add(expense.getAmount());
            expense.setApplicated(true);
            expenseRepository.save(expense);
            log.info("Applied expense ID: {} with amount {}", expense.getId(),expense.getAmount());
        }
        return total;
    }

    private BigDecimal applyPendingIncomes(List<Income> pendingIncomes) {
        BigDecimal total = BigDecimal.ZERO;

        for (Income income : pendingIncomes) {
            total = total.add(income.getAmount());
            income.setApplicated(true);
            incomeRepository.save(income);
            log.info("Applied expense ID: {} with amount {}", income.getId(),income.getAmount());
        }
        return total;
    }
}
