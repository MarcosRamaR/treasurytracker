package com.mvm.transaction.service;

import com.mvm.transaction.dto.IncomeDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import com.mvm.transaction.mapper.IncomeMapper;
import com.mvm.transaction.model.Income;
import com.mvm.transaction.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncomeServiceTest {
    @Mock
    private IncomeRepository incomeRepository;
    @Mock
    private IncomeMapper incomeMapper;
    @InjectMocks
    private IncomeService incomeService;

    private Income testIncome;
    private IncomeDTO testIncomeDTO;
    private IncomeResponseDTO testIncomeResponseDTO;

    @BeforeEach
    void setUp() {
        testIncome = new Income();
        testIncome.setId(1L);
        testIncome.setAmount(new BigDecimal("2000.00"));
        testIncome.setDescription("Salary");
        testIncome.setCategory("Employment");
        testIncome.setDate(LocalDate.now());
        testIncome.setUserId(123L);

        testIncomeDTO = new IncomeDTO();
        testIncomeDTO.setAmount(new BigDecimal("2000.00"));
        testIncomeDTO.setDescription("Salary");
        testIncomeDTO.setCategory("Employment");
        testIncomeDTO.setDate(LocalDate.now());

        testIncomeResponseDTO = new IncomeResponseDTO();
        testIncomeResponseDTO.setId(1L);
        testIncomeResponseDTO.setAmount(new BigDecimal("2000.00"));
        testIncomeResponseDTO.setDescription("Salary");
        testIncomeResponseDTO.setCategory("Employment");
        testIncomeResponseDTO.setDate(LocalDate.now());
        testIncomeResponseDTO.setUserId(123L);
    }

    @Test
    void getAllIncomes_ShouldReturnListOfIncomes() {
        //---Arrange---
        Long userId = 123L;
        List<Income> incomes = Arrays.asList(testIncome);
        when(incomeRepository.findByUserId(userId)).thenReturn(incomes);
        when(incomeMapper.toResponseDTO(testIncome)).thenReturn(testIncomeResponseDTO);

        //---Act---
        List<IncomeResponseDTO> result = incomeService.getAllIncomes(userId);

        //---Assert---
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testIncomeResponseDTO, result.get(0));
        verify(incomeRepository).findByUserId(userId);
        verify(incomeMapper).toResponseDTO(testIncome);
    }

    @Test
    void getIncomeById_ShouldReturnIncome_WhenExistsAndBelongsToUser() {
        Long incomeId = 1L;
        Long userId = 123L;
        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(testIncome));
        when(incomeMapper.toResponseDTO(testIncome)).thenReturn(testIncomeResponseDTO);

        IncomeResponseDTO result = incomeService.getIncomeById(incomeId, userId);

        assertNotNull(result);
        assertEquals(testIncomeResponseDTO, result);
        verify(incomeRepository).findById(incomeId);
        verify(incomeMapper).toResponseDTO(testIncome);
    }


    @Test
    void createIncome_ShouldCreateAndReturnIncome() {
        Long userId = 123L;
        Income savedIncome = new Income();
        savedIncome.setId(1L);
        savedIncome.setUserId(userId);
        when(incomeMapper.toEntity(testIncomeDTO)).thenReturn(testIncome);
        when(incomeRepository.save(testIncome)).thenReturn(savedIncome);
        when(incomeMapper.toResponseDTO(savedIncome)).thenReturn(testIncomeResponseDTO);

        IncomeResponseDTO result = incomeService.createIncome(testIncomeDTO, userId);

        assertNotNull(result);
        assertEquals(testIncomeResponseDTO, result);
        assertEquals(userId, testIncome.getUserId());
        verify(incomeMapper).toEntity(testIncomeDTO);
        verify(incomeRepository).save(testIncome);
        verify(incomeMapper).toResponseDTO(savedIncome);
    }

    @Test
    void updateIncome_ShouldUpdateAndReturnIncome_WhenAuthorized() {
        Long incomeId = 1L;
        Long userId = 123L;
        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(testIncome));
        when(incomeRepository.save(testIncome)).thenReturn(testIncome);
        when(incomeMapper.toResponseDTO(testIncome)).thenReturn(testIncomeResponseDTO);

        IncomeResponseDTO result = incomeService.updateIncome(incomeId, testIncomeDTO, userId);

        assertNotNull(result);
        assertEquals(testIncomeResponseDTO, result);
        verify(incomeRepository).findById(incomeId);
        verify(incomeMapper).updateEntityFromDTO(testIncomeDTO, testIncome);
        verify(incomeRepository).save(testIncome);
        verify(incomeMapper).toResponseDTO(testIncome);
    }

    @Test
    void deleteIncome_ShouldDelete_WhenAuthorized() {
        Long incomeId = 1L;
        Long userId = 123L;
        when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(testIncome));

        incomeService.deleteIncome(incomeId, userId);

        verify(incomeRepository).findById(incomeId);
        verify(incomeRepository).delete(testIncome);
    }

    @Test
    void filterIncomes_ShouldReturnFilteredIncomes() {
        Long userId = 123L;
        String category = "Employment";
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        BigDecimal minAmount = new BigDecimal("1000");
        BigDecimal maxAmount = new BigDecimal("5000");
        List<Income> incomes = Arrays.asList(testIncome);
        when(incomeRepository.findByFiltersAndUser(
                userId, category, startDate, endDate, minAmount, maxAmount)).thenReturn(incomes);
        when(incomeMapper.toResponseDTO(testIncome)).thenReturn(testIncomeResponseDTO);

        List<IncomeResponseDTO> result = incomeService.filterIncomes(
                userId, category, startDate, endDate, minAmount, maxAmount);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testIncomeResponseDTO, result.get(0));
        verify(incomeRepository).findByFiltersAndUser(userId, category, startDate, endDate, minAmount, maxAmount);
        verify(incomeMapper).toResponseDTO(testIncome);
    }
}