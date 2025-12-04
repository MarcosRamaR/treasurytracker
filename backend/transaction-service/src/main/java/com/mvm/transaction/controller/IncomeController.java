package com.mvm.transaction.controller;

import com.mvm.transaction.dto.IncomeDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import com.mvm.transaction.service.IncomeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//Similar to ExpenseController
@RestController
@RequestMapping("/api/incomes")
public class IncomeController {
    @Autowired
    private IncomeService incomeService;


    @GetMapping
    public ResponseEntity<List<IncomeResponseDTO>> getAllIncomes(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<IncomeResponseDTO> incomes = incomeService.getAllIncomes(userId);
        return ResponseEntity.ok(incomes);
    }
    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponseDTO> getIncomeById(@PathVariable Long id,
                                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            IncomeResponseDTO income = incomeService.getIncomeById(id, userId);
            return ResponseEntity.ok(income);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<IncomeResponseDTO> createIncome(@RequestBody IncomeDTO incomeDTO,
                                                          HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        IncomeResponseDTO createdIncome = incomeService.createIncome(incomeDTO, userId);
        return ResponseEntity.ok(createdIncome);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponseDTO> updateIncome(@PathVariable Long id, @RequestBody IncomeDTO incomeDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            IncomeResponseDTO updatedIncome = incomeService.updateIncome(id, incomeDTO, userId);
            return ResponseEntity.ok(updatedIncome);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            incomeService.deleteIncome(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Unauthorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/filters")
    public ResponseEntity<List<IncomeResponseDTO>> filterIncomes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        List<IncomeResponseDTO> incomes = incomeService.filterIncomes(
                userId, category, startDate, endDate, minAmount, maxAmount);

        return ResponseEntity.ok(incomes);
    }

}


