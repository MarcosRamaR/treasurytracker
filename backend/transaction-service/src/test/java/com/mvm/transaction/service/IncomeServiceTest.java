package com.mvm.transaction.service;

import com.mvm.transaction.dto.TransactionRequestDTO;
import com.mvm.transaction.dto.TransactionResponseDTO;
import com.mvm.transaction.mapper.TransactionMapper;
import com.mvm.transaction.model.Transaction;
import com.mvm.transaction.model.TransactionType;
import com.mvm.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private BalanceService balanceService;
    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;
    private TransactionRequestDTO testRequestDTO;
    private TransactionResponseDTO testResponseDTO;
    private final Long userId = 123L;
    private final Long transactionId = 1L;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction(
                new BigDecimal("500.00"), "Salary", "Salary",
                LocalDate.now(), userId, TransactionType.INCOME
        );
        testTransaction.setId(transactionId);
        testTransaction.setApplicated(true);

        testRequestDTO = TransactionRequestDTO.builder()
                .type(TransactionType.INCOME)
                .amount(new BigDecimal("500.00"))
                .description("Salary")
                .category("Salary")
                .date(LocalDate.now())
                .build();

        testResponseDTO = TransactionResponseDTO.builder()
                .id(transactionId)
                .type(TransactionType.INCOME)
                .amount(new BigDecimal("500.00"))
                .description("Salary")
                .category("Salary")
                .date(LocalDate.now())
                .userId(userId)
                .applicated(true)
                .build();
    }

    @Test
    void getIncomeById_ShouldReturnTransaction_WhenExistsAndBelongsToUser() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));
        when(transactionMapper.toResponseDTO(testTransaction)).thenReturn(testResponseDTO);

        TransactionResponseDTO result = transactionService.getTransactionById(transactionId, userId);

        assertNotNull(result);
        assertEquals(testResponseDTO, result);
        verify(transactionRepository).findById(transactionId);
        verify(transactionMapper).toResponseDTO(testTransaction);
    }

    @Test
    void getIncomeById_ShouldThrow_WhenNotExists() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(com.mvm.transaction.exception.TransactionNotFoundException.class,
                () -> transactionService.getTransactionById(transactionId, userId));
    }

    @Test
    void createIncome_ShouldCreateAndReturnTransaction() {
        when(transactionMapper.toEntity(testRequestDTO)).thenReturn(testTransaction);
        when(transactionRepository.save(testTransaction)).thenReturn(testTransaction);
        when(transactionMapper.toResponseDTO(testTransaction)).thenReturn(testResponseDTO);

        TransactionResponseDTO result = transactionService.createTransaction(testRequestDTO, userId);

        assertNotNull(result);
        assertEquals(testResponseDTO, result);
        verify(transactionMapper).toEntity(testRequestDTO);
        verify(transactionRepository).save(testTransaction);
        verify(transactionMapper).toResponseDTO(testTransaction);
    }

    @Test
    void deleteIncome_ShouldDelete_WhenAuthorized() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransaction(transactionId, userId);

        verify(transactionRepository).delete(testTransaction);
    }
}