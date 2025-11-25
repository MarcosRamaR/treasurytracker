package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.dto.ExpenseDTO;
import com.mrr.treasury_tracker.dto.ExpenseResponseDTO;
import com.mrr.treasury_tracker.model.Expense;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.ExpenseRepository;
import com.mrr.treasury_tracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private ExpenseController expenseController;

    private User testUser;
    private Expense testExpense;
    private ExpenseDTO testExpenseDTO;

    @BeforeEach
    void setUp(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test5@test.com");
        testUser.setUserName("Test User");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setDescription("Gasoline");
        testExpense.setAmount(new BigDecimal("200.00"));
        testExpense.setCategory("Transport");
        testExpense.setDate((LocalDate.now()));
        testExpense.setCreatedAt(LocalDateTime.now());
        testExpense.setUser(testUser);

        testExpenseDTO = new ExpenseDTO();
        testExpenseDTO.setDescription("Gasoline");
        testExpenseDTO.setAmount(new BigDecimal("200.00"));
        testExpenseDTO.setCategory("Transport");
        testExpenseDTO.setDate(LocalDate.now());

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test5@test.com");
        when(userService.findByEmail("test5@test.com")).thenReturn(testUser);
    }

    @Test
    void getAllExpenses_ReturnListOfExpenses(){
        List<Expense> expensesList = Collections.singletonList(testExpense);
        when(expenseRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(expensesList);
        List<ExpenseResponseDTO> result = expenseController.getAllExpenses(authentication);

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("Gasoline",result.getFirst().getDescription());
        assertEquals(new BigDecimal("200.00"),result.getFirst().getAmount());
        verify(expenseRepository,times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    void getExpensesById_ReturnExpense_WhenExpenseExists(){
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        ResponseEntity<ExpenseResponseDTO> result = expenseController.getExpenseById(1L,authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Gasoline",result.getBody().getDescription());
        assertEquals((new BigDecimal("200.00")),result.getBody().getAmount());
        assertEquals(1L,result.getBody().getId());
    }

    @Test
    void createExpense_CreateAndReturnExpense(){
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);
        ResponseEntity<?> result = expenseController.createExpense(testExpenseDTO,authentication);

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertNotNull(result.getBody());
        ExpenseResponseDTO response = (ExpenseResponseDTO) result.getBody();
        assertEquals("Gasoline",response.getDescription());
        verify(expenseRepository,times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_UpdateAndReturnExpnese_WhenExpenseExists(){
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        ExpenseDTO updateDTO = new ExpenseDTO();
        updateDTO.setDescription("Other transport expense");
        updateDTO.setAmount(new BigDecimal("400.00"));
        updateDTO.setCategory("Transport");
        updateDTO.setDate(LocalDate.now());

        Expense updatedExpense = new Expense();
        updatedExpense.setDescription("Other transport expense");
        updatedExpense.setAmount(new BigDecimal("400.00"));
        updatedExpense.setCategory("Transport");
        updatedExpense.setDate(LocalDate.now());
        updatedExpense.setCreatedAt(LocalDateTime.now());
        updatedExpense.setUser(testUser);

        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedExpense);
        ResponseEntity<?> result = expenseController.updateExpense(1L,updateDTO,authentication);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        ExpenseResponseDTO response = (ExpenseResponseDTO) result.getBody();
        assertEquals("Other transport expense", ((ExpenseResponseDTO) result.getBody()).getDescription());
        assertEquals(new BigDecimal("400.00"),((ExpenseResponseDTO) result.getBody()).getAmount());
        verify(expenseRepository,times(1)).save(any(Expense.class));
    }

    @Test
    void deleteExpense_WhenExpenseExists(){
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        doNothing().when(expenseRepository).deleteById(1L);
        ResponseEntity<Expense> result = expenseController.deleteExpense(1L,authentication);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(expenseRepository,times(1)).deleteById(1L);
    }

    @Test
    void filterExpense_ReturnFilteredExpenses(){
        List<Expense> expensesList = Collections.singletonList(testExpense);
        when(expenseRepository.findByFiltersAndUser(
                eq(1L),
                eq("Transport"),
                any(LocalDate.class),
                any(LocalDate.class),
                any(BigDecimal.class),
                any(BigDecimal.class))).thenReturn(expensesList);

        List<ExpenseResponseDTO> result = expenseController.filterExpenses(
                "Transport",
                LocalDate.now().minusDays(30),
                LocalDate.now(),
                new BigDecimal("100.00"),
                new BigDecimal("600.00"),
                authentication);

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals("Gasoline",result.getFirst().getDescription());
        assertEquals((new BigDecimal("200.00")),result.getFirst().getAmount());
    }

}
