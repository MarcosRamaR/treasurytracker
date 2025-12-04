package com.mvm.transaction.controller;

import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {
    @Mock
    private ExpenseService expenseService;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private ExpenseController expenseController;

    private ExpenseResponseDTO testExpenseResponseDTO;

    @BeforeEach
    void setUp() {
        testExpenseResponseDTO = new ExpenseResponseDTO();
        testExpenseResponseDTO.setId(1L);
        testExpenseResponseDTO.setAmount(new BigDecimal("100.50"));
        testExpenseResponseDTO.setDescription("Test expense");
        testExpenseResponseDTO.setCategory("Food");
        testExpenseResponseDTO.setDate(LocalDate.now());
        testExpenseResponseDTO.setUserId(123L);

        //We set the userId as attribute for request
        when(request.getAttribute("userId")).thenReturn(123L);
    }

    @Test
    void getAllExpenses_ShouldReturnListOfExpenses() {
        List<ExpenseResponseDTO> expenses = Arrays.asList(testExpenseResponseDTO);
        when(expenseService.getAllExpenses(123L)).thenReturn(expenses);

        ResponseEntity<List<ExpenseResponseDTO>> response = expenseController.getAllExpenses(request);

        assertEquals(HttpStatus.OK, response.getStatusCode()); //Should be 200 ok
        assertNotNull(response.getBody());//Cant be null
        assertEquals(1, response.getBody().size());
        assertEquals(testExpenseResponseDTO, response.getBody().get(0));
        verify(expenseService).getAllExpenses(123L);
    }

    @Test
    void getExpenseById_ShouldReturnExpense_WhenExists() {
        when(expenseService.getExpenseById(1L, 123L)).thenReturn(testExpenseResponseDTO);

        ResponseEntity<ExpenseResponseDTO> response = expenseController.getExpenseById(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testExpenseResponseDTO, response.getBody());
        verify(expenseService).getExpenseById(1L, 123L);
    }

    @Test
    void createExpense_ShouldCreateAndReturnExpense() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(new BigDecimal("100.50"));
        expenseDTO.setDescription("Test expense");
        expenseDTO.setCategory("Food");
        expenseDTO.setDate(LocalDate.now());
        when(expenseService.createExpense(expenseDTO, 123L)).thenReturn(testExpenseResponseDTO);

        ResponseEntity<ExpenseResponseDTO> response = expenseController.createExpense(expenseDTO, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testExpenseResponseDTO, response.getBody());
        verify(expenseService).createExpense(expenseDTO, 123L);
    }

    @Test
    void updateExpense_ShouldUpdateAndReturnExpense_WhenAuthorized() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(new BigDecimal("150.00"));
        expenseDTO.setDescription("Updated expense");

        when(expenseService.updateExpense(1L, expenseDTO, 123L)).thenReturn(testExpenseResponseDTO);

        ResponseEntity<ExpenseResponseDTO> response = expenseController.updateExpense(1L, expenseDTO, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testExpenseResponseDTO, response.getBody());
        verify(expenseService).updateExpense(1L, expenseDTO, 123L);
    }

    @Test
    void deleteExpense_ShouldDelete_WhenAuthorized() {
        ResponseEntity<Void> response = expenseController.deleteExpense(1L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); //Should return 204 no content
        verify(expenseService).deleteExpense(1L, 123L);
    }

    @Test
    void filterExpenses_ShouldReturnFilteredExpenses() {
        String category = "Food";
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        BigDecimal minAmount = new BigDecimal("10");
        BigDecimal maxAmount = new BigDecimal("1000");
        List<ExpenseResponseDTO> expenses = Arrays.asList(testExpenseResponseDTO);

        //eq() to verify exact params
        when(expenseService.filterExpenses(
                eq(123L), eq(category), eq(startDate), eq(endDate),
                eq(minAmount), eq(maxAmount))).thenReturn(expenses);

        ResponseEntity<List<ExpenseResponseDTO>> response =
                expenseController.filterExpenses(category, startDate, endDate, minAmount, maxAmount, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(expenseService).filterExpenses(123L, category, startDate, endDate, minAmount, maxAmount);
    }
}


