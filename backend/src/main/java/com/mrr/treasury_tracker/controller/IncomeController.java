package com.mrr.treasury_tracker.controller;


import com.mrr.treasury_tracker.dto.IncomeDTO;
import com.mrr.treasury_tracker.model.Income;
import com.mrr.treasury_tracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @GetMapping
    public List<Income> getAllIncomes(){
        return incomeRepository.findAll();
    }

    @PostMapping
    public Income createIncome(@RequestBody IncomeDTO requestIncome){
        LocalDate date = requestIncome.getDate();
        if(date == null){
            date=LocalDate.now();
        }
        Income newIncome = new Income(requestIncome.getDescription(), requestIncome.getAmount(), requestIncome.getCategory(), date);
        return incomeRepository.save(newIncome);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Income> updateIncome(@PathVariable Long id, @RequestBody IncomeDTO requestIncome){
        Optional<Income> income = incomeRepository.findById(id);
        if(income.isPresent()){
            Income newIncome = income.get();
            newIncome.setDescription(requestIncome.getDescription());
            newIncome.setAmount(requestIncome.getAmount());
            newIncome.setCategory(requestIncome.getCategory());
            newIncome.setDate(requestIncome.getDate());

            Income updateIncome = incomeRepository.save(newIncome);
            return ResponseEntity.ok(updateIncome);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Income> deleteIncome(@PathVariable Long id){
        if(incomeRepository.existsById(id)){
            incomeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/filters")
    public List<Income> filterIncomes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount){
        return incomeRepository.findByFilters(category, startDate, endDate, minAmount, maxAmount);
    }

    @GetMapping("/total")
    public double getTotalIncomes(){
        List<Income> incomes = incomeRepository.findAll();
        double total= 0;
        for(Income income: incomes){
            total += income.getAmount().doubleValue();
        }
        return total;
    }

}
