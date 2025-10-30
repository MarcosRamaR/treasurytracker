package com.mrr.treasury_tracker.controller;


import com.mrr.treasury_tracker.dto.IncomeDTO;
import com.mrr.treasury_tracker.dto.IncomeResponseDTO;
import com.mrr.treasury_tracker.model.Income;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.IncomeRepository;
import com.mrr.treasury_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserService userService;

    //Get current user from the token
    private User getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); //getPrincipal return a UserDetail from Spring Security
        return userService.findByEmail(userDetails.getUsername());
    }

    @GetMapping
    public List<IncomeResponseDTO> getAllIncomes(Authentication authentication){
        User user = getCurrentUser(authentication);
        //Search on DB all incomes for user
        List<Income> incomes = incomeRepository.findByUserIdOrderByDateDesc(user.getId());

        //Convert the income on IncomeResponseDTO
        List<IncomeResponseDTO> response = new ArrayList<>();
        for (Income income : incomes) {
            IncomeResponseDTO dto = new IncomeResponseDTO();
            dto.setId(income.getId());
            dto.setDescription(income.getDescription());
            dto.setAmount(income.getAmount());
            dto.setCategory(income.getCategory());
            dto.setDate(income.getDate());
            dto.setCreatedAt(income.getCreatedAt());
            dto.setUserEmail(income.getUser().getEmail());
            response.add(dto);
        }
        return response;
    }

    @PostMapping
    public ResponseEntity<?> createIncome(@RequestBody IncomeDTO requestIncome, Authentication authentication){
        User user = getCurrentUser(authentication);
        LocalDate date = requestIncome.getDate();
        if(date == null){
            date=LocalDate.now();
        }

        Income newIncome = new Income(
                requestIncome.getDescription(),
                requestIncome.getAmount(),
                requestIncome.getCategory(),
                date,
                user);
        Income savedIncome =  incomeRepository.save(newIncome);

        IncomeResponseDTO response = new IncomeResponseDTO();
        response.setId(savedIncome.getId());
        response.setDescription(savedIncome.getDescription());
        response.setAmount(savedIncome.getAmount());
        response.setCategory(savedIncome.getCategory());
        response.setDate(savedIncome.getDate());
        response.setCreatedAt(savedIncome.getCreatedAt());
        response.setUserEmail(savedIncome.getUser().getEmail());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(
            @PathVariable Long id, @RequestBody IncomeDTO requestIncome, Authentication authentication){
        User user = getCurrentUser(authentication);
        Optional<Income> income = incomeRepository.findById(id);

        //If this Income exists and is for current user
        if(income.isPresent()&& income.get().getUser().getId().equals(user.getId())){
            Income newIncome = income.get();
            newIncome.setDescription(requestIncome.getDescription());
            newIncome.setAmount(requestIncome.getAmount());
            newIncome.setCategory(requestIncome.getCategory());
            newIncome.setDate(requestIncome.getDate());

            Income updatedIncome = incomeRepository.save(newIncome);

            IncomeResponseDTO response = new IncomeResponseDTO();
            response.setId(updatedIncome.getId());
            response.setDescription(updatedIncome.getDescription());
            response.setAmount(updatedIncome.getAmount());
            response.setCategory(updatedIncome.getCategory());
            response.setDate(updatedIncome.getDate());
            response.setCreatedAt(updatedIncome.getCreatedAt());
            response.setUserEmail(updatedIncome.getUser().getEmail());

            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Income> deleteIncome(@PathVariable Long id, Authentication authentication){
        User user = getCurrentUser(authentication);
        Optional<Income> income = incomeRepository.findById(id);

        if(income.isPresent() && income.get().getUser().getId().equals(user.getId())){
            incomeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filters")
    public List<IncomeResponseDTO> filterIncomes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            Authentication authentication){
        User user = getCurrentUser(authentication);

        List<Income> incomes = incomeRepository.findByFilters(user.getId(), category, startDate, endDate, minAmount, maxAmount);

        //Convert the income on IncomeResponseDTO
        List<IncomeResponseDTO> response = new ArrayList<>();
        for (Income income : incomes) {
            IncomeResponseDTO dto = new IncomeResponseDTO();
            dto.setId(income.getId());
            dto.setDescription(income.getDescription());
            dto.setAmount(income.getAmount());
            dto.setCategory(income.getCategory());
            dto.setDate(income.getDate());
            dto.setCreatedAt(income.getCreatedAt());
            dto.setUserEmail(income.getUser().getEmail());
            response.add(dto);
        }
        return response;
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotalIncomes(Authentication authentication){
        User user = getCurrentUser(authentication);
        BigDecimal total = incomeRepository.getTotalByUser(user.getId()).orElse(BigDecimal.ZERO);
        return ResponseEntity.ok(Map.of("total", total));
    }

}
