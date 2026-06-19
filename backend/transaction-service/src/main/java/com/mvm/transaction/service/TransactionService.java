package com.mvm.transaction.service;

import com.mvm.transaction.dto.AllTransactionsDTO;
import com.mvm.transaction.dto.TransactionRequestDTO;
import com.mvm.transaction.dto.TransactionResponseDTO;
import com.mvm.transaction.exception.AccessDeniedException;
import com.mvm.transaction.exception.TransactionNotFoundException;
import com.mvm.transaction.mapper.TransactionMapper;
import com.mvm.transaction.model.Transaction;
import com.mvm.transaction.model.TransactionType;
import com.mvm.transaction.repository.TransactionRepository;
import com.mvm.transaction.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final String TRANSACTIONS_CACHE = "userTransactions";

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final BalanceService balanceService;

    @Cacheable(value = TRANSACTIONS_CACHE, key = "#userId + '_' + #type + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<TransactionResponseDTO> getAllTransactions(Long userId, TransactionType type, Pageable pageable) {
        log.debug("Cache miss for user: {} type: {}", userId, type);
        return transactionRepository.findByUserIdAndType(userId, type, pageable)
                .map(transactionMapper::toResponseDTO);
    }

    @Cacheable(value = TRANSACTIONS_CACHE, key = "#userId + '_ALL_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<TransactionResponseDTO> getAllTransactions(Long userId, Pageable pageable) {
        log.debug("Cache miss for user: {} (all types)", userId);
        return transactionRepository.findByUserId(userId, pageable)
                .map(transactionMapper::toResponseDTO);
    }

    public TransactionResponseDTO getTransactionById(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id, userId));
        validateOwnership(transaction, userId);
        return transactionMapper.toResponseDTO(transaction);
    }

    @Transactional
    @CacheEvict(value = TRANSACTIONS_CACHE, allEntries = true)
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto, Long userId) {
        log.info("Creating new {} for user: {}", dto.getType(), userId);
        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setUserId(userId);

        LocalDate today = LocalDate.now();
        if (!transaction.getDate().isAfter(today)) {
            applyToBalance(transaction);
            transaction.setApplicated(true);
        }

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created successfully. ID: {}, Type: {}", saved.getId(), saved.getType());
        return transactionMapper.toResponseDTO(saved);
    }

    @Transactional
    @CacheEvict(value = TRANSACTIONS_CACHE, allEntries = true)
    public TransactionResponseDTO updateTransaction(Long id, TransactionRequestDTO dto, Long userId) {
        log.info("Updating transaction {} for user: {}", id, userId);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id, userId));
        validateOwnership(transaction, userId);

        boolean wasApplicated = transaction.isApplicated();
        BigDecimal oldAmount = transaction.getAmount();
        LocalDate oldDate = transaction.getDate();

        transactionMapper.updateEntityFromDTO(dto, transaction);

        handleBalanceOnUpdate(transaction, wasApplicated, oldAmount, oldDate);

        Transaction updated = transactionRepository.save(transaction);
        log.info("Transaction updated successfully. ID: {}", updated.getId());
        return transactionMapper.toResponseDTO(updated);
    }

    @Transactional
    @CacheEvict(value = TRANSACTIONS_CACHE, allEntries = true)
    public void deleteTransaction(Long id, Long userId) {
        log.info("Deleting transaction {} for user: {}", id, userId);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id, userId));
        validateOwnership(transaction, userId);

        if (transaction.isApplicated()) {
            revertFromBalance(transaction);
        }

        transactionRepository.delete(transaction);
    }

    public Page<TransactionResponseDTO> filterTransactions(
            Long userId, TransactionType type,
            String description, String category,
            LocalDate startDate, LocalDate endDate,
            BigDecimal minAmount, BigDecimal maxAmount,
            Boolean applicated, Pageable pageable) {

        Specification<Transaction> spec = Specification.where(TransactionSpecification.byUserId(userId))
                .and(TransactionSpecification.byType(type))
                .and(TransactionSpecification.byDescription(description))
                .and(TransactionSpecification.byCategory(category))
                .and(TransactionSpecification.byDateRange(startDate, endDate))
                .and(TransactionSpecification.byAmountRange(minAmount, maxAmount))
                .and(TransactionSpecification.byApplicated(applicated));

        return transactionRepository.findAll(spec, pageable)
                .map(transactionMapper::toResponseDTO);
    }

    @Transactional
    @CacheEvict(value = TRANSACTIONS_CACHE, allEntries = true)
    public int deleteFilteredTransactions(
            Long userId, TransactionType type,
            String description, String category,
            LocalDate startDate, LocalDate endDate,
            BigDecimal minAmount, BigDecimal maxAmount) {

        List<Transaction> toDelete = transactionRepository.findAll(
                Specification.where(TransactionSpecification.byUserId(userId))
                        .and(TransactionSpecification.byType(type))
                        .and(TransactionSpecification.byDescription(description))
                        .and(TransactionSpecification.byCategory(category))
                        .and(TransactionSpecification.byDateRange(startDate, endDate))
                        .and(TransactionSpecification.byAmountRange(minAmount, maxAmount))
        );

        for (Transaction t : toDelete) {
            if (t.isApplicated()) {
                revertFromBalance(t);
            }
        }

        int deletedCount = toDelete.size();
        if (deletedCount > 0) {
            transactionRepository.deleteAll(toDelete);
            log.info("Deleted {} filtered transactions for user: {}", deletedCount, userId);
        }
        return deletedCount;
    }

    public List<AllTransactionsDTO> getAllTransactionsForExport(Long userId) {
        return transactionRepository.findAllByUserId(userId).stream()
                .map(this::toAllTransactionsDTO)
                .toList();
    }

    public List<AllTransactionsDTO> getFilteredTransactionsForExport(
            Long userId, TransactionType type,
            String description, String category,
            LocalDate startDate, LocalDate endDate,
            BigDecimal minAmount, BigDecimal maxAmount) {

        Specification<Transaction> spec = Specification.where(TransactionSpecification.byUserId(userId))
                .and(TransactionSpecification.byType(type))
                .and(TransactionSpecification.byDescription(description))
                .and(TransactionSpecification.byCategory(category))
                .and(TransactionSpecification.byDateRange(startDate, endDate))
                .and(TransactionSpecification.byAmountRange(minAmount, maxAmount));

        return transactionRepository.findAll(spec).stream()
                .map(this::toAllTransactionsDTO)
                .toList();
    }

    private void validateOwnership(Transaction transaction, Long userId) {
        if (!transaction.getUserId().equals(userId)) {
            throw new AccessDeniedException(transaction.getId(), userId, transaction.getType().name());
        }
    }

    private void applyToBalance(Transaction transaction) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            balanceService.updateBalanceFromNewExpense(transaction.getUserId(), transaction.getAmount());
        } else {
            balanceService.updateBalanceFromNewIncome(transaction.getUserId(), transaction.getAmount());
        }
    }

    private void revertFromBalance(Transaction transaction) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            balanceService.revertExpense(transaction.getUserId(), transaction.getAmount());
        } else {
            balanceService.revertIncome(transaction.getUserId(), transaction.getAmount());
        }
    }

    private void handleBalanceOnUpdate(Transaction transaction, boolean wasApplicated, BigDecimal oldAmount, LocalDate oldDate) {
        LocalDate today = LocalDate.now();
        boolean nowApplicated = !transaction.getDate().isAfter(today);

        if (wasApplicated && nowApplicated) {
            BigDecimal diff = transaction.getAmount().subtract(oldAmount);
            if (transaction.getType() == TransactionType.EXPENSE) {
                balanceService.updateBalanceFromNewExpense(transaction.getUserId(), diff.negate());
            } else {
                balanceService.updateBalanceFromNewIncome(transaction.getUserId(), diff);
            }
        } else if (wasApplicated && !nowApplicated) {
            revertFromBalance(transaction);
        } else if (!wasApplicated && nowApplicated) {
            applyToBalance(transaction);
        }
    }

    private AllTransactionsDTO toAllTransactionsDTO(Transaction t) {
        return AllTransactionsDTO.builder()
                .id(t.getId())
                .type(t.getType())
                .amount(t.getAmount())
                .description(t.getDescription())
                .category(t.getCategory())
                .date(t.getDate())
                .userId(t.getUserId())
                .build();
    }
}