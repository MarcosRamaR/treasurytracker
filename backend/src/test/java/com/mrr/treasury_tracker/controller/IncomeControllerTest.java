package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.dto.IncomeDTO;
import com.mrr.treasury_tracker.dto.IncomeResponseDTO;
import com.mrr.treasury_tracker.model.Income;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.IncomeRepository;
import com.mrr.treasury_tracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncomeControllerTest {

    @Mock
    private IncomeRepository incomeRepository; //Sim repository

    @Mock
    private UserService userService; //Sim service

    @Mock
    private Authentication authentication; //Sim authentication (spring)

    @Mock
    private UserDetails userDetails; //Sim user details (spring)

    @InjectMocks //Create real object and inject the false (mock) objects
    private IncomeController incomeController;

    private User testUser;
    private Income testIncome;
    private IncomeDTO testIncomeDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test4@test.com");
        testUser.setUserName("Test User");

        testIncome = new Income();
        testIncome.setId(1L);
        testIncome.setDescription("January Salary");
        testIncome.setAmount(new BigDecimal("3000.00"));
        testIncome.setCategory("Salary");
        testIncome.setDate(LocalDate.now());
        testIncome.setCreatedAt(LocalDateTime.now());
        testIncome.setUser(testUser);

        testIncomeDTO = new IncomeDTO();
        testIncomeDTO.setDescription("January Salary");
        testIncomeDTO.setAmount(new BigDecimal("3000.00"));
        testIncomeDTO.setCategory("Salary");
        testIncomeDTO.setDate(LocalDate.now());

        //Emulate obtain user since authentication token
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test4@test.com");
        when(userService.findByEmail("test4@test.com")).thenReturn(testUser);
    }

    @Test
    void getAllIncomes_ReturnListOfIncomes() {
        List<Income> incomes = Collections.singletonList(testIncome);
        when(incomeRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(incomes);

        List<IncomeResponseDTO> result = incomeController.getAllIncomes(authentication);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("January Salary", result.get(0).getDescription());
        assertEquals(new BigDecimal("3000.00"), result.get(0).getAmount());

        //verify to check the method was called one time
        verify(incomeRepository, times(1)).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    void getAllIncomes_ReturnEmptyList_WhenNoIncomesExist() {
        //Sim the BD return a void list
        when(incomeRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(new ArrayList<>());

        List<IncomeResponseDTO> result = incomeController.getAllIncomes(authentication);

        assertNotNull(result); //List exists
        assertTrue(result.isEmpty());  //List empty
    }

    
}
