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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

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
                new BigDecimal("100.50"), "Test expense", "Food",
                LocalDate.now(), userId, TransactionType.EXPENSE
        );
        testTransaction.setId(transactionId);
        testTransaction.setApplicated(true);

        testRequestDTO = TransactionRequestDTO.builder()
                .type(TransactionType.EXPENSE)
                .amount(new BigDecimal("100.50"))
                .description("Test expense")
                .category("Food")
                .date(LocalDate.now())
                .build();

        testResponseDTO = TransactionResponseDTO.builder()
                .id(transactionId)
                .type(TransactionType.EXPENSE)
                .amount(new BigDecimal("100.50"))
                .description("Test expense")
                .category("Food")
                .date(LocalDate.now())
                .userId(userId)
                .applicated(true)
                .build();
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenExistsAndBelongsToUser() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));
        when(transactionMapper.toResponseDTO(testTransaction)).thenReturn(testResponseDTO);

        TransactionResponseDTO result = transactionService.getTransactionById(transactionId, userId);

        assertNotNull(result);
        assertEquals(testResponseDTO, result);
        verify(transactionRepository).findById(transactionId);
        verify(transactionMapper).toResponseDTO(testTransaction);
    }

    @Test
    void getTransactionById_ShouldThrow_WhenNotExists() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(com.mvm.transaction.exception.TransactionNotFoundException.class,
                () -> transactionService.getTransactionById(transactionId, userId));
    }

    @Test
    void getTransactionById_ShouldThrow_WhenNotOwned() {
        Long otherUserId = 999L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));

        assertThrows(com.mvm.transaction.exception.AccessDeniedException.class,
                () -> transactionService.getTransactionById(transactionId, otherUserId));
    }

    @Test
    void createTransaction_ShouldCreateAndReturnTransaction() {
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
    void deleteTransaction_ShouldDelete_WhenAuthorizedAndNotApplicated() {
        testTransaction.setApplicated(false);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransaction(transactionId, userId);

        verify(transactionRepository).delete(testTransaction);
        verify(balanceService, never()).revertExpense(any(), any());
    }

    @Test
    void deleteTransaction_ShouldRevertBalance_WhenApplicated() {
        testTransaction.setApplicated(true);
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransaction(transactionId, userId);

        verify(balanceService).revertExpense(userId, testTransaction.getAmount());
        verify(transactionRepository).delete(testTransaction);
    }
}