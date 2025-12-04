package com.mvm.transaction.mapper;

import com.mvm.transaction.dto.IncomeDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import com.mvm.transaction.model.Income;
import org.springframework.stereotype.Component;

@Component
public class IncomeMapper {

    public IncomeResponseDTO toResponseDTO(Income income) {
        if (income == null) {
            return null;
        }

        IncomeResponseDTO dto = new IncomeResponseDTO();
        dto.setId(income.getId());
        dto.setAmount(income.getAmount());
        dto.setDescription(income.getDescription());
        dto.setCategory(income.getCategory());
        dto.setDate(income.getDate());
        dto.setUserId(income.getUserId());
        return dto;
    }

    public Income toEntity(IncomeDTO dto) {
        if (dto == null) {
            return null;
        }

        Income income = new Income();
        income.setAmount(dto.getAmount());
        income.setDescription(dto.getDescription());
        income.setCategory(dto.getCategory());
        income.setDate(dto.getDate());
        return income;
    }

    public void updateEntityFromDTO(IncomeDTO dto, Income income) {
        if (dto == null || income == null) {
            return;
        }

        income.setAmount(dto.getAmount());
        income.setDescription(dto.getDescription());
        income.setCategory(dto.getCategory());
        income.setDate(dto.getDate());
    }
}