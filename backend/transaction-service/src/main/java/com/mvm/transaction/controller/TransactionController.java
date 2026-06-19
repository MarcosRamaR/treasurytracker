package com.mvm.transaction.controller;

import com.mvm.transaction.dto.TransactionRequestDTO;
import com.mvm.transaction.dto.TransactionResponseDTO;
import com.mvm.transaction.model.TransactionType;
import com.mvm.transaction.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<TransactionResponseDTO> transactions;
        if (type != null) {
            transactions = transactionService.getAllTransactions(userId, type, pageable);
        } else {
            transactions = transactionService.getAllTransactions(userId, pageable);
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionResponseDTO transaction = transactionService.getTransactionById(id, userId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequestDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionResponseDTO created = transactionService.createTransaction(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionResponseDTO updated = transactionService.updateTransaction(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filters")
    public ResponseEntity<Page<TransactionResponseDTO>> filterTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) Boolean applicated,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<TransactionResponseDTO> expenses = transactionService.filterTransactions(
                userId, type, description, category, startDate, endDate,
                minAmount, maxAmount, applicated, pageable);
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/delete-filtered")
    public ResponseEntity<Map<String, Object>> deleteFilteredTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        boolean haveOneFilter = (description != null && !description.trim().isEmpty())
                || (category != null && !category.trim().isEmpty())
                || (startDate != null)
                || (endDate != null)
                || (minAmount != null)
                || (maxAmount != null);

        if (!haveOneFilter) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "NO_FILTERS",
                    "message", "At least one filter must be provided"
            ));
        }

        int deletedCount = transactionService.deleteFilteredTransactions(
                userId, type, description, category, startDate, endDate,
                minAmount, maxAmount);
        return ResponseEntity.ok(Map.of(
                "deletedCount", deletedCount,
                "message", "Deleted " + deletedCount + (type != null ? " " + type.name().toLowerCase() : "") + " transactions"
        ));
    }
}