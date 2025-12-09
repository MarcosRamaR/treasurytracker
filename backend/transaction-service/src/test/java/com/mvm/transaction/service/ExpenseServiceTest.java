package com.mvm.transaction.service;


import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.mapper.ExpenseMapper;
import com.mvm.transaction.model.Expense;
import com.mvm.transaction.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {
    @Mock //Creates a simulated object, not a real one
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseMapper expenseMapper;
    @InjectMocks //Creates a real instance but with the false mocks
    private ExpenseService expenseService;

    private Expense testExpense;
    private ExpenseDTO testExpenseDTO;
    private ExpenseResponseDTO testExpenseResponseDTO;

    @BeforeEach
    void setUp() {
        //Create an expense for tests
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setAmount(new BigDecimal("100.50"));
        testExpense.setDescription("Test expense");
        testExpense.setCategory("Food");
        testExpense.setDate(LocalDate.now());
        testExpense.setUserId(123L);

        //Creates an DTO for create or update expenses
        testExpenseDTO = new ExpenseDTO();
        testExpenseDTO.setAmount(new BigDecimal("100.50"));
        testExpenseDTO.setDescription("Test expense");
        testExpenseDTO.setCategory("Food");
        testExpenseDTO.setDate(LocalDate.now());

        //Creates a response DTO
        testExpenseResponseDTO = new ExpenseResponseDTO();
        testExpenseResponseDTO.setId(1L);
        testExpenseResponseDTO.setAmount(new BigDecimal("100.50"));
        testExpenseResponseDTO.setDescription("Test expense");
        testExpenseResponseDTO.setCategory("Food");
        testExpenseResponseDTO.setDate(LocalDate.now());
        testExpenseResponseDTO.setUserId(123L);
    }

    @Test
    void getAllExpenses_ShouldReturnListOfExpenses() {
        // ---Arrange---
        Long userId = 123L;
        List<Expense> expenses = Collections.singletonList(testExpense); //Creates one element list with our expense (setUP)

        //when call findByUserId, return expenses list
        when(expenseRepository.findByUserId(userId)).thenReturn(expenses);
        when(expenseMapper.toResponseDTO(testExpense)).thenReturn(testExpenseResponseDTO);

        // ---Act---
        //Call real method we want test
        List<ExpenseResponseDTO> result = expenseService.getAllExpenses(userId);

        // ---Assert---
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExpenseResponseDTO, result.get(0)); //The first element must be the element we want

        verify(expenseRepository).findByUserId(userId); //Verify findByUserId with right userId was called
        verify(expenseMapper).toResponseDTO(testExpense);
    }

    @Test
    void getExpenseById_ShouldReturnExpense_WhenExistsAndBelongsToUser() {
        Long expenseId = 1L;
        Long userId = 123L;
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(testExpense));
        when(expenseMapper.toResponseDTO(testExpense)).thenReturn(testExpenseResponseDTO);

        ExpenseResponseDTO result = expenseService.getExpenseById(expenseId, userId);

        assertNotNull(result);
        assertEquals(testExpenseResponseDTO, result);
        verify(expenseRepository).findById(expenseId);
        verify(expenseMapper).toResponseDTO(testExpense);
    }

    @Test
    void createExpense_ShouldCreateAndReturnExpense() {
        Long userId = 123L;
        //Creates a saved expense
        Expense savedExpense = new Expense();
        savedExpense.setId(1L);
        savedExpense.setUserId(userId);
        when(expenseMapper.toEntity(testExpenseDTO)).thenReturn(testExpense); //DTO to entity
        when(expenseRepository.save(testExpense)).thenReturn(savedExpense); //Repository save the expense and return it
        when(expenseMapper.toResponseDTO(savedExpense)).thenReturn(testExpenseResponseDTO); //Entity to ResponseDTO

        ExpenseResponseDTO result = expenseService.createExpense(testExpenseDTO, userId);

        assertNotNull(result);
        assertEquals(testExpenseResponseDTO, result); //Returned expense must be the same we want
        verify(expenseMapper).toEntity(testExpenseDTO);
        verify(expenseRepository).save(testExpense);
        verify(expenseMapper).toResponseDTO(savedExpense);
    }

    @Test
    void updateExpense_ShouldUpdateAndReturnExpense_WhenAuthorized() {
        Long expenseId = 1L;
        Long userId = 123L;

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(testExpense));//We find the expense
        when(expenseRepository.save(testExpense)).thenReturn(testExpense); //Repository save changes
        when(expenseMapper.toResponseDTO(testExpense)).thenReturn(testExpenseResponseDTO); //To ResponseDTO

        ExpenseResponseDTO result = expenseService.updateExpense(expenseId, testExpenseDTO, userId);

        assertNotNull(result);
        assertEquals(testExpenseResponseDTO, result);
        verify(expenseRepository).findById(expenseId);
        verify(expenseMapper).updateEntityFromDTO(testExpenseDTO, testExpense);
        verify(expenseRepository).save(testExpense);
        verify(expenseMapper).toResponseDTO(testExpense);
    }

    @Test
    void deleteExpense_ShouldDelete_WhenAuthorized() {
        Long expenseId = 1L;
        Long userId = 123L;
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(testExpense));

        expenseService.deleteExpense(expenseId, userId);

        verify(expenseRepository).findById(expenseId);
        verify(expenseRepository).delete(testExpense);
    }

    @Test
    void filterExpenses_ShouldReturnFilteredExpenses() {
        Long userId = 123L;
        String description = "Test expense";
        String category = "Food";
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        BigDecimal minAmount = new BigDecimal("10");
        BigDecimal maxAmount = new BigDecimal("1000");
        List<Expense> expenses = Arrays.asList(testExpense);

        //Set the mock with specific params
        when(expenseRepository.findByFiltersAndUser(
                userId, description, category, startDate, endDate, minAmount, maxAmount)).thenReturn(expenses);
        when(expenseMapper.toResponseDTO(testExpense)).thenReturn(testExpenseResponseDTO);

        List<ExpenseResponseDTO> result = expenseService.filterExpenses(
                userId,description, category, startDate, endDate, minAmount, maxAmount);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExpenseResponseDTO, result.get(0));
        verify(expenseRepository).findByFiltersAndUser(userId,description, category, startDate, endDate, minAmount, maxAmount);
        verify(expenseMapper).toResponseDTO(testExpense);
    }
}
