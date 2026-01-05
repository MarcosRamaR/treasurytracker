package com.mvm.transaction.service;


import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.mapper.ExpenseMapper;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private static final String EXPENSES_CACHE = "userExpenses"; //name for caching

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseMapper expenseMapper;
    @Autowired
    private BalanceService balanceService;

    @Cacheable(value = EXPENSES_CACHE, key = "#userId") //We want set this on cache
    public List<ExpenseResponseDTO> getAllExpenses(Long userId){
        System.out.println("Cache MISS. Getting data from DB for user: " + userId);
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return expenses.stream()
                .map(expenseMapper::toResponseDTO)//For each element use this -> :: for implicit lambda
                .collect(Collectors.toList());
    }

    @Cacheable(value = EXPENSES_CACHE, key = "#userId + '_' + #id")
    public ExpenseResponseDTO getExpenseById(Long id, Long userId){
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        if(!expense.getUserId().equals(userId)){
            throw new RuntimeException("Unauthorized acces to expense");
        }
        return expenseMapper.toResponseDTO(expense);
    }

    @Transactional //All operation need be complete to save
    @CacheEvict(value = EXPENSES_CACHE, key = "#userId") //With new data, "delete" cache
    public ExpenseResponseDTO createExpense(ExpenseDTO expenseDTO, Long userId) {
        System.out.println("Cache EVICT. Create new expense, invalidate the cache for user: " + userId);
        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense.setUserId(userId);
        LocalDate today = LocalDate.now();
        if(!expense.getDate().isAfter(today)){
            balanceService.updateBalanceFromNewExpense(userId, expense.getAmount());
            expense.setApplicated(true);
        }
        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponseDTO(savedExpense);
    }

    @Transactional
    @CacheEvict(value = EXPENSES_CACHE, key = "#userId")
    public ExpenseResponseDTO updateExpense(Long id, ExpenseDTO expenseDTO, Long userId) {
        System.out.println("Cache EVICT. Updating expense, invalidate the cache for user: " + userId);
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        if (!expense.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized update of expense");
        }
        expenseMapper.updateEntityFromDTO(expenseDTO, expense);
        Expense updatedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponseDTO(updatedExpense);
    }

    @Transactional
    @CacheEvict(value = EXPENSES_CACHE, key = "#userId")
    public void deleteExpense(Long id, Long userId) {
        System.out.println("Cache EVICT. Delete expense, invalidate the cache for user: " + userId);
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        if (!expense.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized deletion of expense");
        }
        expenseRepository.delete(expense);
    }

    public List<ExpenseResponseDTO> filterExpenses(
            Long userId,
            String description,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount) {
        List<Expense> expenses = expenseRepository.findByFiltersAndUser(userId,description, category, startDate, endDate, minAmount, maxAmount);
        return expenses.stream().map(expenseMapper::toResponseDTO).collect(Collectors.toList());
    }
}
