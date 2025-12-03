package com.mvm.transaction.controller;

import com.mvm.transaction.dto.IncomeDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.model.Income;
import com.mvm.transaction.repository.IncomeRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

//Similar to ExpenseController
@RestController
@RequestMapping("/api/incomes")
public class IncomeController {
    @Autowired
    private IncomeRepository incomeRepository;


    @GetMapping
    public ResponseEntity<List<IncomeResponseDTO>> getAllIncomes(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        List<Income> incomes = incomeRepository.findByUserId(userId);
        List<IncomeResponseDTO> incomeDTOs = incomes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(incomeDTOs);
    }

    @PostMapping
    public ResponseEntity<IncomeResponseDTO> createIncome(@RequestBody IncomeDTO incomeDTO,
                                                          HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        Income income = convertToEntity(incomeDTO);
        income.setUserId(userId);
        Income savedIncome = incomeRepository.save(income);
        return ResponseEntity.ok(convertToResponseDTO(savedIncome));
    }
    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponseDTO> updateIncome(@PathVariable Long id, @RequestBody IncomeDTO incomeDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + id));

        if (!income.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        income.setAmount(incomeDTO.getAmount());
        income.setDescription(incomeDTO.getDescription());
        income.setCategory(incomeDTO.getCategory());
        income.setDate(incomeDTO.getDate());

        Income updatedIncome = incomeRepository.save(income);
        return ResponseEntity.ok(convertToResponseDTO(updatedIncome));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + id));

        if (!income.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        incomeRepository.delete(income);
        return ResponseEntity.noContent().build();
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
        List<Income> incomes = incomeRepository.findByFiltersAndUser(userId, category,startDate,endDate,minAmount,maxAmount);

        List<IncomeResponseDTO> response = incomes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    private Income convertToEntity(IncomeDTO dto) {
        Income income = new Income();
        income.setAmount(dto.getAmount());
        income.setDescription(dto.getDescription());
        income.setCategory(dto.getCategory());
        income.setDate(dto.getDate());
        return income;
    }

    private IncomeResponseDTO convertToResponseDTO(Income income) {
        IncomeResponseDTO dto = new IncomeResponseDTO();
        dto.setId(income.getId());
        dto.setAmount(income.getAmount());
        dto.setDescription(income.getDescription());
        dto.setCategory(income.getCategory());
        dto.setDate(income.getDate());
        dto.setUserId(income.getUserId());
        return dto;
    }
}
