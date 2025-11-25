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

        //Emulates authentication
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test4@test.com");
        when(userService.findByEmail("test4@test.com")).thenReturn(testUser);
    }

    @Test
    void getAllIncomes_ReturnListOfIncomes() {
        List<Income> incomes = Collections.singletonList(testIncome); //Inmutable one element list
        when(incomeRepository.findByUserIdOrderByDateDesc(1L)).thenReturn(incomes);

        //Call real method, but using mock repository (we get previous list)
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

    @Test
    void getIncomeById_ReturnIncome_WhenIncomeExists() {
        //Sim we find the income on DB
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(testIncome));

        ResponseEntity<IncomeResponseDTO> result = incomeController.getIncomeById(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode()); //200 status code
        assertNotNull(result.getBody()); //Body with data
        assertEquals("January Salary",result.getBody().getDescription());
        assertEquals(1L,result.getBody().getId());
    }

    @Test
    void getIncomeById_ReturnNotFound_WhenIncomeDoesNotExist() {
        when(incomeRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<IncomeResponseDTO> result = incomeController.getIncomeById(1L, authentication);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); //400 status code
        assertNull(result.getBody()); //Not data
    }

    @Test
    void getIncomeById_ReturnNotFound_WhenIncomeDoesNotBelongToUser() {
        //Creates income from other user
        User otherUser = new User();
        otherUser.setId(2L);  //Different user id
        testIncome.setUser(otherUser);

        when(incomeRepository.findById(1L)).thenReturn(Optional.of(testIncome));

        //User with Id=1 try wee the income to user with id=2
        ResponseEntity<IncomeResponseDTO> result = incomeController.getIncomeById(1L, authentication);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
    @Test
    void createIncome_CreateAndReturnIncome() {
        //Sim the repository answer
        when(incomeRepository.save(any(Income.class))).thenReturn(testIncome);

        ResponseEntity<?> result = incomeController.createIncome(testIncomeDTO, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());

        //(IncomeResponseDTO) due to we know is this object type
        IncomeResponseDTO response = (IncomeResponseDTO) result.getBody();
        assertEquals("January Salary", response.getDescription());

        verify(incomeRepository, times(1)).save(any(Income.class));
    }

    @Test
    void updateIncome_UpdateAndReturnIncome_WhenIncomeExists() {
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(testIncome));

        //New data
        IncomeDTO updateDTO = new IncomeDTO();
        updateDTO.setDescription("Updated Salary");  // Descripción nueva
        updateDTO.setAmount(new BigDecimal("3500.00"));    // Monto nuevo
        updateDTO.setCategory("Salary");
        updateDTO.setDate(LocalDate.now());

        Income updatedIncome = new Income();
        updatedIncome.setId(1L);
        updatedIncome.setDescription("Updated Salary");
        updatedIncome.setAmount(new BigDecimal("3500.00"));
        updatedIncome.setCategory("Salary");
        updatedIncome.setDate(LocalDate.now());
        updatedIncome.setCreatedAt(LocalDateTime.now());
        updatedIncome.setUser(testUser);

        //Set mock to return updated income
        when(incomeRepository.save(any(Income.class))).thenReturn(updatedIncome);

        ResponseEntity<?> result = incomeController.updateIncome(1L, updateDTO, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        IncomeResponseDTO response = (IncomeResponseDTO) result.getBody();
        assertEquals("Updated Salary", response.getDescription());
        assertEquals(new BigDecimal("3500.00"), response.getAmount());
        verify(incomeRepository, times(1)).save(any(Income.class));
    }

    @Test
    void deleteIncome_DeleteIncome_WhenIncomeExists() {
        when(incomeRepository.findById(1L)).thenReturn(Optional.of(testIncome));

        //Tell mockito do nothing special with method, "it's ok", necessary due to method without return data
        doNothing().when(incomeRepository).deleteById(1L);

        ResponseEntity<Income> result = incomeController.deleteIncome(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(incomeRepository, times(1)).deleteById(1L);
    }

    @Test
    void filterIncomes_ReturnFilteredIncomes() {
        List<Income> incomes = Collections.singletonList(testIncome);

        // eq() = "equal to"
        // any() = "any value"
        when(incomeRepository.findByFilters(
                eq(1L),
                eq("Salary"),
                any(LocalDate.class),
                any(LocalDate.class),
                any(BigDecimal.class),
                any(BigDecimal.class))).thenReturn(incomes);

        List<IncomeResponseDTO> result = incomeController.filterIncomes(
                "Salary",
                LocalDate.now().minusDays(30),
                LocalDate.now(),
                new BigDecimal("1000.00"),
                new BigDecimal("5000.00"),
                authentication);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("January Salary", result.getFirst().getDescription());
    }

    @Test
    void getTotalIncomes_ReturnTotal() {
        BigDecimal total = new BigDecimal("10000.00");
        when(incomeRepository.getTotalByUser(1L)).thenReturn(Optional.of(total));

        ResponseEntity<?> result = incomeController.getTotalIncomes(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        @SuppressWarnings("unchecked") //To ignore cast warning
        Map<String, BigDecimal> body = (Map<String, BigDecimal>) result.getBody();

        assertNotNull(body);
        assertEquals(total, body.get("total"));
    }

    @Test
    void getCategoryTotals_ReturnCategoryTotals() {
        List<Object[]> categoryTotals = new ArrayList<>();

        //Object[] is a row from the result
        Object[] category1 = {"Salary", new BigDecimal("5000.00")};
        Object[] category2 = {"Investments", new BigDecimal("2000.00")};
        categoryTotals.add(category1);
        categoryTotals.add(category2);

        //Emulates two rows, Salary and Transport
        when(incomeRepository.getCategoryTotalsByUser(1L)).thenReturn(categoryTotals);

        //Get user authenticated (id=1), call method to get an Object[] list, return
        ResponseEntity<List<Object[]>> result = incomeController.getCategoryTotals(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size()); //We expected two categories

        assertEquals("Salary", result.getBody().get(0)[0]);
        assertEquals(new BigDecimal("5000.00"), result.getBody().get(0)[1]);
        assertEquals("Investments", result.getBody().get(1)[0]);
        assertEquals(new BigDecimal("2000.00"), result.getBody().get(1)[1]);
    }
}
