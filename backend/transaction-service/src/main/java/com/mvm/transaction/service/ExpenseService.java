package com.mvm.transaction.service;


import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.mapper.ExpenseMapper;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseMapper expenseMapper;

    public List<ExpenseResponseDTO> getAllExpenses(Long userId){
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        return expenses.stream()
                .map(expenseMapper::toResponseDTO)//For each element use this -> :: for implicit lambda
                .collect(Collectors.toList());
    }

    public ExpenseResponseDTO getExpenseById(Long id, Long userId){
        Expense expense = expenseRepository.findById(userId).orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        if(!expense.getUserId().equals(userId)){
            throw new RuntimeException("Unauthorized acces to expense");
        }
        return expenseMapper.toResponseDTO(expense);
    }

    @Transactional //All operation need be complete to save
    public ExpenseResponseDTO createExpense(ExpenseDTO expenseDTO, Long userId) {
        Expense expense = expenseMapper.toEntity(expenseDTO);
        expense.setUserId(userId);
        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponseDTO(savedExpense);
    }
    @Transactional
    public ExpenseResponseDTO updateExpense(Long id, ExpenseDTO expenseDTO, Long userId) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        if (!expense.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized update of expense");
        }
        expenseMapper.updateEntityFromDTO(expenseDTO, expense);
        Expense updatedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponseDTO(updatedExpense);
    }
    @Transactional
    public void deleteExpense(Long id, Long userId) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        if (!expense.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized deletion of expense");
        }
        expenseRepository.delete(expense);
    }
    public List<ExpenseResponseDTO> filterExpenses(
            Long userId,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount) {
        List<Expense> expenses = expenseRepository.findByFiltersAndUser(userId, category, startDate, endDate, minAmount, maxAmount);
        return expenses.stream().map(expenseMapper::toResponseDTO).collect(Collectors.toList());
    }
}
