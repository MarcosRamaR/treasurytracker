package com.mvm.transaction.controller;

import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(@RequestHeader("X-User-Id") Long userId) {
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        List<ExpenseResponseDTO> expenseDTOs = expenses.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expenseDTOs);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO, 
                                                          @RequestHeader("X-User-Id") Long userId) {
        Expense expense = convertToEntity(expenseDTO);
        expense.setUserId(userId);
        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(convertToResponseDTO(savedExpense));
    }

    private Expense convertToEntity(ExpenseDTO dto) {
        Expense expense = new Expense();
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setCategory(dto.getCategory());
        expense.setDate(dto.getDate());
        return expense;
    }

    private ExpenseResponseDTO convertToResponseDTO(Expense expense) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setCategory(expense.getCategory());
        dto.setDate(expense.getDate());
        dto.setUserId(expense.getUserId());
        return dto;
    }
}
