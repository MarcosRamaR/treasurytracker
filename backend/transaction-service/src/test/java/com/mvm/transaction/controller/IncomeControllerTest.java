package com.mvm.transaction.controller;

import com.mvm.transaction.dto.IncomeDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import com.mvm.transaction.service.IncomeService;
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
public class IncomeControllerTest {
    @Mock
    private IncomeService incomeService;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private IncomeController incomeController;
    private IncomeResponseDTO testIncomeResponseDTO;

    @BeforeEach
    void setUp() {
        testIncomeResponseDTO = new IncomeResponseDTO();
        testIncomeResponseDTO.setId(1L);
        testIncomeResponseDTO.setAmount(new BigDecimal("2000.00"));
        testIncomeResponseDTO.setDescription("Salary");
        testIncomeResponseDTO.setCategory("Employment");
        testIncomeResponseDTO.setDate(LocalDate.now());
        testIncomeResponseDTO.setUserId(123L);

        when(request.getAttribute("userId")).thenReturn(123L);
    }

    @Test
    void getAllIncomes_ShouldReturnListOfIncomes() {
        //---Arrange---
        List<IncomeResponseDTO> incomes = Arrays.asList(testIncomeResponseDTO);
        when(incomeService.getAllIncomes(123L)).thenReturn(incomes);

        //---Act---
        ResponseEntity<List<IncomeResponseDTO>> response = incomeController.getAllIncomes(request);

        //---Assert---
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(incomeService).getAllIncomes(123L);
    }

    @Test
    void getIncomeById_ShouldReturnIncome_WhenExists() {
        when(incomeService.getIncomeById(1L, 123L)).thenReturn(testIncomeResponseDTO);

        ResponseEntity<IncomeResponseDTO> response = incomeController.getIncomeById(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testIncomeResponseDTO, response.getBody());
        verify(incomeService).getIncomeById(1L, 123L);
    }

    @Test
    void createIncome_ShouldCreateAndReturnIncome() {
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setAmount(new BigDecimal("2000.00"));
        incomeDTO.setDescription("Salary");
        when(incomeService.createIncome(incomeDTO, 123L)).thenReturn(testIncomeResponseDTO);

        ResponseEntity<IncomeResponseDTO> response = incomeController.createIncome(incomeDTO, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testIncomeResponseDTO, response.getBody());
        verify(incomeService).createIncome(incomeDTO, 123L);
    }

    @Test
    void updateIncome_ShouldUpdateAndReturnIncome() {
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setAmount(new BigDecimal("2500.00"));
        incomeDTO.setDescription("Updated Salary");
        when(incomeService.updateIncome(1L, incomeDTO, 123L)).thenReturn(testIncomeResponseDTO);

        ResponseEntity<IncomeResponseDTO> response = incomeController.updateIncome(1L, incomeDTO, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testIncomeResponseDTO, response.getBody());
        verify(incomeService).updateIncome(1L, incomeDTO, 123L);
    }

    @Test
    void deleteIncome_ShouldDelete() {
        ResponseEntity<Void> response = incomeController.deleteIncome(1L, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(incomeService).deleteIncome(1L, 123L);
    }

    @Test
    void filterIncomes_ShouldReturnFilteredIncomes() {
        String category = "Employment";
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        BigDecimal minAmount = new BigDecimal("1000");
        BigDecimal maxAmount = new BigDecimal("5000");
        List<IncomeResponseDTO> incomes = Arrays.asList(testIncomeResponseDTO);
        when(incomeService.filterIncomes(
                eq(123L), eq(category), eq(startDate), eq(endDate),
                eq(minAmount), eq(maxAmount))).thenReturn(incomes);

        ResponseEntity<List<IncomeResponseDTO>> response = incomeController.filterIncomes(
                category, startDate, endDate, minAmount, maxAmount, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(incomeService).filterIncomes(123L, category, startDate, endDate, minAmount, maxAmount);
    }
}