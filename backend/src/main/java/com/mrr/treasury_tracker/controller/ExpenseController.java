package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.dto.ExpenseDTO;
import com.mrr.treasury_tracker.dto.ExpenseResponseDTO;
import com.mrr.treasury_tracker.model.Expense;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.ExpenseRepository;
import com.mrr.treasury_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private  ExpenseRepository expenseRepository;

    @Autowired
    private  UserService userService;


    private User getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @GetMapping
    public List<ExpenseResponseDTO> getAllExpenses(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(user.getId());

        List<ExpenseResponseDTO> response = new ArrayList<>();
        for (Expense expense : expenses) {
            ExpenseResponseDTO dto = new ExpenseResponseDTO();
            dto.setId(expense.getId());
            dto.setDescription(expense.getDescription());
            dto.setAmount(expense.getAmount());
            dto.setCategory(expense.getCategory());
            dto.setDate(expense.getDate());
            dto.setCreatedAt(expense.getCreatedAt());
            dto.setUserEmail(expense.getUser().getEmail());
            response.add(dto);
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Expense> expense = expenseRepository.findById(id);

        //If this expense exists and is for current user
        if (expense.isPresent() && expense.get().getUser().getId().equals(user.getId())) {
            Expense expenseData = expense.get();
            ExpenseResponseDTO response = new ExpenseResponseDTO();
            response.setId(expenseData.getId());
            response.setDescription(expenseData.getDescription());
            response.setAmount(expenseData.getAmount());
            response.setCategory(expenseData.getCategory());
            response.setDate(expenseData.getDate());
            response.setCreatedAt(expenseData.getCreatedAt());
            response.setUserEmail(expenseData.getUser().getEmail());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createExpense(@RequestBody ExpenseDTO requestExpense, Authentication authentication) {
        User user = getCurrentUser(authentication);
        LocalDate date = requestExpense.getDate();
        if(date == null){
            date=LocalDate.now();
        }
        Expense newExpense = new Expense(
                requestExpense.getDescription(),
                requestExpense.getAmount(),
                requestExpense.getCategory(),
                date,
                user
        );

        Expense savedExpense = expenseRepository.save(newExpense);

        ExpenseResponseDTO response = new ExpenseResponseDTO();
        response.setId(savedExpense.getId());
        response.setDescription(savedExpense.getDescription());
        response.setAmount(savedExpense.getAmount());
        response.setCategory(savedExpense.getCategory());
        response.setDate(savedExpense.getDate());
        response.setCreatedAt(savedExpense.getCreatedAt());
        response.setUserEmail(savedExpense.getUser().getEmail());

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @PathVariable Long id, @RequestBody ExpenseDTO request, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Expense> expense = expenseRepository.findById(id);

        if (expense.isPresent() && expense.get().getUser().getId().equals(user.getId())) {
            Expense existingExpense = expense.get();
            existingExpense.setDescription(request.getDescription());
            existingExpense.setAmount(request.getAmount());
            existingExpense.setCategory(request.getCategory());
            existingExpense.setDate(request.getDate());

            Expense updatedExpense = expenseRepository.save(existingExpense);

            ExpenseResponseDTO response = new ExpenseResponseDTO();
            response.setId(updatedExpense.getId());
            response.setDescription(updatedExpense.getDescription());
            response.setAmount(updatedExpense.getAmount());
            response.setCategory(updatedExpense.getCategory());
            response.setDate(updatedExpense.getDate());
            response.setCreatedAt(updatedExpense.getCreatedAt());
            response.setUserEmail(updatedExpense.getUser().getEmail());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Optional<Expense> expense = expenseRepository.findById(id);

        if (expense.isPresent() && expense.get().getUser().getId().equals(user.getId())) {
            expenseRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/filters")
    public List<ExpenseResponseDTO> filterExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            Authentication authentication) {

        User user = getCurrentUser(authentication);
        List<Expense> expenses = expenseRepository.findByFiltersAndUser(
                user.getId(), category, startDate, endDate, minAmount, maxAmount);

        List<ExpenseResponseDTO> response = new ArrayList<>();
        for (Expense expense : expenses) {
            ExpenseResponseDTO dto = new ExpenseResponseDTO();
            dto.setId(expense.getId());
            dto.setDescription(expense.getDescription());
            dto.setAmount(expense.getAmount());
            dto.setCategory(expense.getCategory());
            dto.setDate(expense.getDate());
            dto.setCreatedAt(expense.getCreatedAt());
            dto.setUserEmail(expense.getUser().getEmail());
            response.add(dto);
        }
        return response;
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalExpenses(Authentication authentication) {
        User user = getCurrentUser(authentication);
        BigDecimal total = expenseRepository.getTotalByUser(user.getId()).orElse(BigDecimal.ZERO);
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/category-totals")
    public ResponseEntity<List<Object[]>> getCategoryTotals(Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Object[]> categoryTotals = expenseRepository.getCategoryTotalsByUser(user.getId());
        return ResponseEntity.ok(categoryTotals);
    }
}