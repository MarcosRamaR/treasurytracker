package com.mvm.transaction.controller;

import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    //userId now is obtained from JWT

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ExpenseResponseDTO> expenses = expenseService.getAllExpenses(userId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            ExpenseResponseDTO expense = expenseService.getExpenseById(id, userId);
            return ResponseEntity.ok(expense);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO,
                                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ExpenseResponseDTO createdExpense = expenseService.createExpense(expenseDTO, userId);
        return ResponseEntity.ok(createdExpense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        try {
            ExpenseResponseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO, userId);
            return ResponseEntity.ok(updatedExpense);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            expenseService.deleteExpense(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
        List<ExpenseResponseDTO> expenses = expenseService.filterExpenses(
                userId, category, startDate, endDate, minAmount, maxAmount);

        return ResponseEntity.ok(expenses);
    }

}



