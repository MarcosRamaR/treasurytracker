package com.mrr.treasury_tracker.service;

import com.mrr.treasury_tracker.model.Balance;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.BalanceRepository;
import com.mrr.treasury_tracker.repository.ExpenseRepository;
import com.mrr.treasury_tracker.repository.IncomeRepository;
import com.mrr.treasury_tracker.model.Expense;
import com.mrr.treasury_tracker.model.Income;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public BalanceService(BalanceRepository balanceRepository,
                          IncomeRepository incomeRepository,
                          ExpenseRepository expenseRepository) {
        this.balanceRepository = balanceRepository;
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
    }
    public Balance getBalance(User user) {
        return balanceRepository.findByUserId(user.getId()).orElse(null);
    }

    public Balance updateBalanceManual(User user, BigDecimal newAmount) {
        Optional<Balance> balanceOpt = balanceRepository.findByUserId(user.getId());
        if (balanceOpt.isEmpty()) return null;

        Balance balance = balanceOpt.get();
        balance.setAmount(newAmount);
        balance.setModifiedBy("MANUAL");
        balance.setUpdatedAt(LocalDateTime.now());

        return balanceRepository.save(balance);
    }

    public Balance updateBalanceAutomatically(User user) {
        Optional<Balance> balanceOpt = balanceRepository.findByUserId(user.getId());
        if (balanceOpt.isEmpty()) return null;

        Balance balance = balanceOpt.get();

        List<Income> incomes = incomeRepository.findPendingIncomesToApply(user.getId());
        List<Expense> expenses = expenseRepository.findPendingExpensesToApply(user.getId());

        BigDecimal totalIncome = incomes.stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newBalanceAmount = balance.getAmount().add(totalIncome).subtract(totalExpense);
        balance.setAmount(newBalanceAmount);
        balance.setModifiedBy("SYSTEM");
        balance.setUpdatedAt(LocalDateTime.now());
        Balance updatedBalance = balanceRepository.save(balance);

        incomes.forEach(i -> i.setApplicated(true));
        expenses.forEach(e -> e.setApplicated(true));
        incomeRepository.saveAll(incomes);
        expenseRepository.saveAll(expenses);

        return updatedBalance;
    }

}
