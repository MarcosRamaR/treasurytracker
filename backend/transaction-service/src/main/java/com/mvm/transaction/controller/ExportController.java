package com.mvm.transaction.controller;

import com.mvm.transaction.model.TransactionType;
import com.mvm.transaction.service.ExportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/{type}/csv")
    public CompletableFuture<ResponseEntity<byte[]>> exportTransactions(
            @PathVariable String type,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionType transactionType = parseType(type);
        return exportService.exportTransactionsToCsv(userId, transactionType)
                .thenApply(bytes -> buildCsvResponse(bytes, type + "-transactions.csv"));
    }

    @GetMapping("/{type}/filtered/csv")
    public CompletableFuture<ResponseEntity<byte[]>> exportFilteredTransactions(
            @PathVariable String type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionType transactionType = parseType(type);
        return exportService.exportFilteredTransactionsToCsv(
                        userId, transactionType, description, category,
                        startDate, endDate, minAmount, maxAmount)
                .thenApply(bytes -> buildCsvResponse(bytes, "filtered-" + type + "-transactions.csv"));
    }

    @GetMapping("/all/transactions/csv")
    public CompletableFuture<ResponseEntity<byte[]>> exportAllTransactions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return exportService.exportAllTransactionsToCsv(userId)
                .thenApply(bytes -> buildCsvResponse(bytes, "all-transactions.csv"));
    }

    @GetMapping("/all/transactions/filtered/csv")
    public CompletableFuture<ResponseEntity<byte[]>> exportAllFilteredTransactions(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return exportService.exportAllFilteredTransactionsToCsv(
                        userId, description, category, startDate, endDate, minAmount, maxAmount)
                .thenApply(bytes -> buildCsvResponse(bytes, "filtered-transactions.csv"));
    }

    private ResponseEntity<byte[]> buildCsvResponse(byte[] csvBytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }

    private TransactionType parseType(String type) {
        return switch (type.toLowerCase()) {
            case "expenses" -> TransactionType.EXPENSE;
            case "incomes" -> TransactionType.INCOME;
            default -> throw new IllegalArgumentException("Invalid transaction type: " + type);
        };
    }
}