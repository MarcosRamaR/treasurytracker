package com.mvm.transaction.service;

import com.mvm.transaction.dto.AllTransactionsDTO;
import com.mvm.transaction.dto.TransactionResponseDTO;
import com.mvm.transaction.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final TransactionService transactionService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String CSV_HEADER = "Date;Type;Description;Category;Amount\n";
    private static final char CSV_SEPARATOR = ';';

    @Async("exportTaskExecutor")
    public CompletableFuture<byte[]> exportTransactionsToCsv(Long userId, TransactionType type) {
        List<TransactionResponseDTO> transactions = transactionService
                .getAllTransactions(userId, type, Pageable.unpaged())
                .getContent();
        return CompletableFuture.completedFuture(generateCsv(transactions, type));
    }

    @Async("exportTaskExecutor")
    public CompletableFuture<byte[]> exportFilteredTransactionsToCsv(
            Long userId, TransactionType type,
            String description, String category,
            LocalDate startDate, LocalDate endDate,
            BigDecimal minAmount, BigDecimal maxAmount) {

        List<TransactionResponseDTO> transactions = transactionService
                .filterTransactions(userId, type, description, category,
                        startDate, endDate, minAmount, maxAmount, null, Pageable.unpaged())
                .getContent();
        return CompletableFuture.completedFuture(generateCsv(transactions, type));
    }

    @Async("exportTaskExecutor")
    public CompletableFuture<byte[]> exportAllTransactionsToCsv(Long userId) {
        List<AllTransactionsDTO> transactions = transactionService.getAllTransactionsForExport(userId);
        return CompletableFuture.completedFuture(generateAllCsv(transactions));
    }

    @Async("exportTaskExecutor")
    public CompletableFuture<byte[]> exportAllFilteredTransactionsToCsv(
            Long userId, String description, String category,
            LocalDate startDate, LocalDate endDate,
            BigDecimal minAmount, BigDecimal maxAmount) {

        List<AllTransactionsDTO> transactions = transactionService.getFilteredTransactionsForExport(
                userId, null, description, category, startDate, endDate, minAmount, maxAmount);
        return CompletableFuture.completedFuture(generateAllCsv(transactions));
    }

    private byte[] generateCsv(List<TransactionResponseDTO> transactions, TransactionType type) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {
            writer.write(CSV_HEADER);
            for (TransactionResponseDTO t : transactions) {
                writer.write(formatLine(t, type));
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV file", e);
        }
        return outputStream.toByteArray();
    }

    private byte[] generateAllCsv(List<AllTransactionsDTO> transactions) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {
            writer.write(CSV_HEADER);
            for (AllTransactionsDTO t : transactions) {
                writer.write(formatLine(t));
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV file", e);
        }
        return outputStream.toByteArray();
    }

    private String formatLine(TransactionResponseDTO t, TransactionType type) {
        StringBuilder line = new StringBuilder();
        line.append(t.getDate().format(DATE_FORMATTER)).append(CSV_SEPARATOR);
        line.append(type.name()).append(CSV_SEPARATOR);
        line.append(escapeCsv(t.getDescription())).append(CSV_SEPARATOR);
        line.append(escapeCsv(t.getCategory())).append(CSV_SEPARATOR);
        if (type == TransactionType.EXPENSE) {
            line.append('-').append(t.getAmount().toString());
        } else {
            line.append(t.getAmount().toString());
        }
        line.append('\n');
        return line.toString();
    }

    private String formatLine(AllTransactionsDTO t) {
        StringBuilder line = new StringBuilder();
        line.append(t.getDate().format(DATE_FORMATTER)).append(CSV_SEPARATOR);
        line.append(t.getType().name()).append(CSV_SEPARATOR);
        line.append(escapeCsv(t.getDescription())).append(CSV_SEPARATOR);
        line.append(escapeCsv(t.getCategory())).append(CSV_SEPARATOR);
        line.append(t.getType() == TransactionType.EXPENSE ? "-" : "")
                .append(t.getAmount().toString()).append('\n');
        return line.toString();
    }

    private String escapeCsv(String value) {
        if (value == null || value.isEmpty()) return "";
        boolean needsEscape = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == CSV_SEPARATOR || c == '"' || c == '\n' || c == '\r') {
                needsEscape = true;
                break;
            }
        }
        if (!needsEscape) return value;

        StringBuilder escaped = new StringBuilder();
        escaped.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"') escaped.append("\"\"");
            else escaped.append(c);
        }
        escaped.append('"');
        return escaped.toString();
    }
}