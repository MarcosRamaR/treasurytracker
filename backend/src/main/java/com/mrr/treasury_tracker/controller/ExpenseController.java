package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.dto.ExpenseDTO;
import com.mrr.treasury_tracker.model.Expense;
import com.mrr.treasury_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping
    public List<Expense> getAllExpenses(){
        return expenseRepository.findAll();
    }

    @GetMapping("/{id}")
    //ResponseEntity allow control http response
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id){
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isPresent()){
            return ResponseEntity.ok(expense.get());
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Expense createExpense(@RequestBody ExpenseDTO request){
        LocalDate date = request.getDate();
        if(date == null){
            date = LocalDate.now();
        }
        Expense newExpense = new Expense(request.getDescription(), request.getAmount(), request.getCategory(), date);
        return expenseRepository.save(newExpense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO request){
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isPresent()){
            Expense newExpense = expense.get();

            newExpense.setDescription(request.getDescription());
            newExpense.setAmount(request.getAmount());
            newExpense.setCategory(request.getCategory());
            newExpense.setDate(request.getDate());

            Expense updateExpense = expenseRepository.save(newExpense);
            return ResponseEntity.ok(updateExpense);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable Long id){
        if(expenseRepository.existsById(id)){
            expenseRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    public List<Expense> getExpensesByCategory(@PathVariable String category){
        return expenseRepository.findByCategoryOrderByDateDesc(category);
    }

    @GetMapping("/between-date")
    public List<Expense> getExpensesByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        return expenseRepository.findByDateBetweenOrderByDateDesc(startDate,endDate);
    }

    @GetMapping("/amount-greater-than/{amount}")
    public List<Expense> getExpensesGreaterThan(@PathVariable BigDecimal amount){
        return expenseRepository.findByAmountGreaterThanOrderByDateDesc(amount);
    }

    @GetMapping("category/{category}/date-range")
    public List<Expense> getExpensesByCategoryAndDateRange(
            @PathVariable String category, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        return expenseRepository.findByCategoryAndDateBetween(category,startDate,endDate);
    }

    @GetMapping("/total")
    public double getTotalExpenses(){
        List<Expense> expenses = expenseRepository.findAll();
        double total = 0;
        for (Expense expense: expenses){
            total += expense.getAmount().doubleValue();
        }
        return total;
    }

    @GetMapping("/total/category/{category}")
    public double getTotalByCategory(@PathVariable String category){
        List<Expense> expenses = expenseRepository.findByCategoryOrderByDateDesc(category);
        double total = 0;
        for (Expense expense: expenses){
            total += expense.getAmount().doubleValue();
        }
        return total;
    }

    @GetMapping("total/date-range")
    public double getTotalByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        List<Expense> expenses = expenseRepository.findByDateBetweenOrderByDateDesc(startDate,endDate);
        double total = 0;
        for (Expense expense: expenses){
            total += expense.getAmount().doubleValue();
        }
        return total;
    }
    @GetMapping("/total/current-month")
    public double getCurrentMonthTotal() {
        List<Expense> expenses = expenseRepository.findCurrentMonthExpenses();
        double total = 0.0;

        for (Expense expense : expenses) {
            total += expense.getAmount().doubleValue();
        }

        return total;
    }

    
}
