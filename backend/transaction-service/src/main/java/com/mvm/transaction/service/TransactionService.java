package com.mvm.transaction.service;

import com.mvm.transaction.dto.AllTransactionsDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private IncomeService incomeService;

    public List<AllTransactionsDTO> getAllTransactions(Long userId) {
        List<ExpenseResponseDTO> allExpenses = expenseService.getAllExpenses(userId);
        List<IncomeResponseDTO> allIncomes = incomeService.getAllIncomes(userId);
        return combineAndSortTransactions(allExpenses, allIncomes, userId);
    }

    public List<AllTransactionsDTO> getFilteredTransactions(Long userId, String description, String category,
                                                            LocalDate startDate, LocalDate endDate,
                                                            BigDecimal minAmount, BigDecimal maxAmount) {

        List<ExpenseResponseDTO> filteredExpenses = expenseService.filterExpenses(
                userId, description, category, startDate, endDate, minAmount, maxAmount);
        List<IncomeResponseDTO> filteredIncomes = incomeService.filterIncomes(
                userId, description, category, startDate, endDate, minAmount, maxAmount);

        return combineAndSortTransactions(filteredExpenses, filteredIncomes, userId);
    }

    private List<AllTransactionsDTO> combineAndSortTransactions(List<ExpenseResponseDTO> expenses,
                                                                List<IncomeResponseDTO> incomes,
                                                                Long userId) {
        List<AllTransactionsDTO> allTransactions = new ArrayList<>();

        //Convert expenses to AllTransactionsDTO
        for (ExpenseResponseDTO expense : expenses) {
            allTransactions.add(convertExpenseToAllTransactionsDTO(expense, userId));
        }
        //Convert incomes to AllTransactionsDTO
        for (IncomeResponseDTO income : incomes) {
            allTransactions.add(convertIncomeToAllTransactionsDTO(income, userId));
        }
        //Sort by date and after by type
        allTransactions.sort((a, b) -> {
            //Date desc
            int dateCompare = b.getDate().compareTo(a.getDate());
            if (dateCompare != 0) {
                return dateCompare;
            }
            //If they have same date, order by income first
            return a.getType().compareTo(b.getType());
        });

        return allTransactions;
    }
    private AllTransactionsDTO convertExpenseToAllTransactionsDTO(ExpenseResponseDTO expense, Long userId) {
        AllTransactionsDTO dto = new AllTransactionsDTO();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount().negate()); //Negative for expense
        dto.setDescription(expense.getDescription());
        dto.setCategory(expense.getCategory());
        dto.setDate(expense.getDate());
        dto.setType("EXPENSE");
        dto.setUserId(userId);
        return dto;
    }

    private AllTransactionsDTO convertIncomeToAllTransactionsDTO(IncomeResponseDTO income, Long userId) {
        AllTransactionsDTO dto = new AllTransactionsDTO();
        dto.setId(income.getId());
        dto.setAmount(income.getAmount());
        dto.setDescription(income.getDescription());
        dto.setCategory(income.getCategory());
        dto.setDate(income.getDate());
        dto.setType("INCOME");
        dto.setUserId(userId);
        return dto;
    }

}
