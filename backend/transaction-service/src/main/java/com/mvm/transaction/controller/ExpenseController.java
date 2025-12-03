package com.mvm.transaction.controller;

import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.repository.ExpenseRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseRepository expenseRepository;

    //Now we obtain userId from JWT filter

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Expense> expenses = expenseRepository.findByUserId(userId);
        List<ExpenseResponseDTO> expenseDTOs = expenses.stream()//To allow better conversion with .map
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());//convert the stream to normal list
        return ResponseEntity.ok(expenseDTOs);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO,
                                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Expense expense = convertToEntity(expenseDTO);
        expense.setUserId(userId); //Link the expense with the user (ExpenseDTO haven't user)
        Expense savedExpense = expenseRepository.save(expense);
        return ResponseEntity.ok(convertToResponseDTO(savedExpense));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        Expense existingExpense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        if(!existingExpense.getUserId().equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existingExpense.setAmount(expenseDTO.getAmount());
        existingExpense.setDescription(expenseDTO.getDescription());
        existingExpense.setCategory(expenseDTO.getCategory());
        existingExpense.setDate(expenseDTO.getDate());

        Expense updatedExpense = expenseRepository.save(existingExpense);
        return ResponseEntity.ok(convertToResponseDTO(updatedExpense));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        if (!expense.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        expenseRepository.delete(expense);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filters")
    public ResponseEntity<List<ExpenseResponseDTO>> filterExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        List<Expense> expenses = expenseRepository.findByFiltersAndUser(
                userId, category, startDate, endDate, minAmount, maxAmount);

        List<ExpenseResponseDTO> response = expenses.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
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
