package com.mvm.transaction.controller;

import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.model.User;
import com.mvm.transaction.repository.ExpenseRepository;
import com.mvm.transaction.service.UserService;
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
import java.util.*;

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

    @Test
    void getTotalExpenses_ReturnTotal(){
        BigDecimal total = new BigDecimal("10000.00");
        when(expenseRepository.getTotalByUser(1L)).thenReturn(Optional.of(total));

        ResponseEntity<?> result = expenseController.getTotalExpenses(authentication);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> body = (Map<String, BigDecimal>) result.getBody();

        assertNotNull(body);
        assertEquals(total,body.get("total"));
    }

    @Test
    void getCategoryTotals_ReturnCategoryExpensesTotals(){
        List<Object[]> categoryTotals = new ArrayList<>();
        Object[] category1 = {"Transport", new BigDecimal("5000.00")};
        Object[] category2 = {"Food", new BigDecimal("2000.00")};
        categoryTotals.add(category1);
        categoryTotals.add(category2);

        when(expenseRepository.getCategoryTotalsByUser(1L)).thenReturn(categoryTotals);
        ResponseEntity<List<Object[]>> result = expenseController.getCategoryTotals(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2,result.getBody().size());
        assertEquals("Transport",result.getBody().get(0)[0]);
        assertEquals(new BigDecimal("5000.00"),result.getBody().get(0)[1]);
        assertEquals("Food",result.getBody().get(1)[0]);
        assertEquals(new BigDecimal("2000.00"),result.getBody().get(1)[1]);

    }

}
