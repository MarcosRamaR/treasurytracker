package com.mrr.treasury_tracker.controller;


import com.mrr.treasury_tracker.dto.IncomeDTO;
import com.mrr.treasury_tracker.model.Income;
import com.mrr.treasury_tracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@CrossOrigin(origins = "http://localhost:5173")
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

}
