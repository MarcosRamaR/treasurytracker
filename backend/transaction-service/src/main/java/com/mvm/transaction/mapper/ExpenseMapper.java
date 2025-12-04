package com.mvm.transaction.mapper;

import com.mvm.transaction.dto.ExpenseDTO;
import com.mvm.transaction.dto.ExpenseResponseDTO;
import com.mvm.transaction.model.Expense;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public ExpenseResponseDTO toResponseDTO(Expense expense) {
        if (expense == null) {
            return null;
        }

        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDescription(expense.getDescription());
        dto.setCategory(expense.getCategory());
        dto.setDate(expense.getDate());
        dto.setUserId(expense.getUserId());
        return dto;
    }

    public Expense toEntity(ExpenseDTO dto) {
        if (dto == null) {
            return null;
        }

        Expense expense = new Expense();
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setCategory(dto.getCategory());
        expense.setDate(dto.getDate());
        return expense;
    }

    public void updateEntityFromDTO(ExpenseDTO dto, Expense expense) {
        if (dto == null || expense == null) {
            return;
        }

        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setCategory(dto.getCategory());
        expense.setDate(dto.getDate());
    }
}
