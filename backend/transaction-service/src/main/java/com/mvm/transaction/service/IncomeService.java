package com.mvm.transaction.service;

import com.mvm.transaction.dto.IncomeDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import com.mvm.transaction.mapper.IncomeMapper;
import com.mvm.transaction.model.Income;
import com.mvm.transaction.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private IncomeMapper incomeMapper;
    @Autowired
    private BalanceService balanceService;

    public List<IncomeResponseDTO> getAllIncomes(Long userId) {
        List<Income> incomes = incomeRepository.findByUserId(userId);
        return incomes.stream().map(incomeMapper::toResponseDTO).collect(Collectors.toList());
    }

    public IncomeResponseDTO getIncomeById(Long id, Long userId) {
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found with id: " + id));
        if (!income.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to income");
        }
        return incomeMapper.toResponseDTO(income);
    }

    @Transactional
    public IncomeResponseDTO createIncome(IncomeDTO incomeDTO, Long userId) {
        Income income = incomeMapper.toEntity(incomeDTO);
        income.setUserId(userId);
        LocalDate today = LocalDate.now();
        if(!income.getDate().isAfter(today)){
            balanceService.updateBalanceFromNewIncome(userId, income.getAmount());
            income.setApplicated(true);
        }
        Income savedIncome = incomeRepository.save(income);
        return incomeMapper.toResponseDTO(savedIncome);
    }

    @Transactional
    public IncomeResponseDTO updateIncome(Long id, IncomeDTO incomeDTO, Long userId) {
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found with id: " + id));
        if (!income.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized update of income");
        }
        incomeMapper.updateEntityFromDTO(incomeDTO, income);
        Income updatedIncome = incomeRepository.save(income);
        return incomeMapper.toResponseDTO(updatedIncome);
    }

    @Transactional
    public void deleteIncome(Long id, Long userId) {
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found with id: " + id));
        if (!income.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized deletion of income");
        }
        incomeRepository.delete(income);
    }

    public List<IncomeResponseDTO> filterIncomes(
            Long userId,
            String description,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount) {

        List<Income> incomes = incomeRepository.findByFiltersAndUser(
                userId,description, category, startDate, endDate, minAmount, maxAmount);
        return incomes.stream().map(incomeMapper::toResponseDTO).collect(Collectors.toList());
    }
}