package com.mvm.transaction.controller;

import com.mvm.transaction.dto.TransactionRequestDTO;
import com.mvm.transaction.dto.TransactionResponseDTO;
import com.mvm.transaction.model.TransactionType;
import com.mvm.transaction.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeControllerTest {

    @Mock
    private TransactionService transactionService;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private TransactionController transactionController;

    private TransactionResponseDTO testResponse;
    private TransactionRequestDTO testRequest;
    private final Long userId = 123L;

    @BeforeEach
    void setUp() {
        when(request.getAttribute("userId")).thenReturn(userId);

        testRequest = TransactionRequestDTO.builder()
                .type(TransactionType.INCOME)
                .amount(new BigDecimal("500.00"))
                .description("Salary")
                .category("Salary")
                .date(LocalDate.now())
                .build();

        testResponse = TransactionResponseDTO.builder()
                .id(1L)
                .type(TransactionType.INCOME)
                .amount(new BigDecimal("500.00"))
                .description("Salary")
                .category("Salary")
                .date(LocalDate.now())
                .userId(userId)
                .applicated(true)
                .build();
    }

    @Test
    void getAllIncomes_ShouldReturnPageOfTransactions() {
        Page<TransactionResponseDTO> page = new PageImpl<>(List.of(testResponse));
        when(transactionService.getAllTransactions(userId, TransactionType.INCOME, PageRequest.of(0, 20)))
                .thenReturn(page);

        ResponseEntity<Page<TransactionResponseDTO>> response =
                transactionController.getAllTransactions(TransactionType.INCOME, PageRequest.of(0, 20), request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(testResponse, response.getBody().getContent().get(0));
    }

    @Test
    void createIncome_ShouldCreateAndReturnTransaction() {
        when(transactionService.createTransaction(testRequest, userId)).thenReturn(testResponse);

        ResponseEntity<TransactionResponseDTO> response =
                transactionController.createTransaction(testRequest, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testResponse, response.getBody());
        verify(transactionService).createTransaction(testRequest, userId);
    }

    @Test
    void deleteIncome_ShouldReturnNoContent() {
        ResponseEntity<Void> response = transactionController.deleteTransaction(1L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService).deleteTransaction(1L, userId);
    }
}