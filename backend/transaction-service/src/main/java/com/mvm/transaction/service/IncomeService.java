package com.mvm.transaction.service;

import com.mvm.transaction.dto.IncomeDTO;
import com.mvm.transaction.dto.IncomeResponseDTO;
import com.mvm.transaction.mapper.IncomeMapper;
import com.mvm.transaction.model.Income;
import com.mvm.transaction.repository.IncomeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IncomeService {

    private static final String INCOMES_CACHE = "userIncomes";

    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private IncomeMapper incomeMapper;
    @Autowired
    private BalanceService balanceService;

    @Cacheable(value = INCOMES_CACHE, key = "#userId")
    public List<IncomeResponseDTO> getAllIncomes(Long userId) {
        log.debug("Cache miss for user: {}", userId);
        log.debug("Querying database for user {} incomes", userId);
        List<Income> incomes = incomeRepository.findByUserId(userId);
        log.debug("Found {} expenses for user: {}", incomes.size(), userId);
        return incomes.stream().map(incomeMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Cacheable(value = INCOMES_CACHE, key = "#userId + '_' + #id")
    public IncomeResponseDTO getIncomeById(Long id, Long userId) {
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found with id: " + id));
        if (!income.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to income");
        }
        return incomeMapper.toResponseDTO(income);
    }

    @Transactional
    @CacheEvict(value = INCOMES_CACHE, key = "#userId")
    public IncomeResponseDTO createIncome(IncomeDTO incomeDTO, Long userId) {
        log.info("Creating new expense and invalidating cache for user: {}", userId);
        log.debug("Expense details: amount={}, category={}, date={}",
                incomeDTO.getAmount(), incomeDTO.getCategory(), incomeDTO.getDate());
        Income income = incomeMapper.toEntity(incomeDTO);
        income.setUserId(userId);
        LocalDate today = LocalDate.now();
        if(!income.getDate().isAfter(today)){
            balanceService.updateBalanceFromNewIncome(userId, income.getAmount());
            income.setApplicated(true);
        }
        Income savedIncome = incomeRepository.save(income);
        log.info("Income created successfully. ID: {}", savedIncome.getId());
        return incomeMapper.toResponseDTO(savedIncome);
    }

    @Transactional
    @CacheEvict(value = INCOMES_CACHE, key = "#userId")
    public IncomeResponseDTO updateIncome(Long id, IncomeDTO incomeDTO, Long userId) {
        log.info("Updating new income and invalidating cache for user: {}", userId);
        Income income = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("Income not found with id: " + id));
        if (!income.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized update of income");
        }
        incomeMapper.updateEntityFromDTO(incomeDTO, income);
        Income updatedIncome = incomeRepository.save(income);
        log.info("Updated income successfully. ID: {}", updatedIncome.getId());
        return incomeMapper.toResponseDTO(updatedIncome);
    }

    @Transactional
    @CacheEvict(value = INCOMES_CACHE, key = "#userId")
    public void deleteIncome(Long id, Long userId) {
        log.info("Deleting new income and invalidating cache for user: {}", userId);
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

    @Transactional
    @CacheEvict(value = INCOMES_CACHE, key = "#userId")
    public int deleteFilteredExpenses(
            Long userId,
            String description,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount) {
        int deletedCount = incomeRepository.deleteByFiltersAndUser(userId,description, category, startDate, endDate, minAmount, maxAmount);

        log.info("Deleted " + deletedCount + " of filtered incomes for user: " +userId);
        return deletedCount;
    }

}