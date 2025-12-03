package com.mvm.transaction.service;

import com.mvm.transaction.dto.BalanceDTO;
import com.mvm.transaction.model.Balance;
import com.mvm.transaction.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;

    public Balance createInitialBalance(Long userId) {
        Balance balance = new Balance();
        balance.setUserId(userId);
        balance.setTotalBalance(BigDecimal.ZERO);
        balance.setModifiedBy("SYSTEM");
        return balanceRepository.save(balance);
    }

    //Method called by controller when auth-service requests the creation of an initial balance
    public BalanceDTO createInitialBalanceForUser(Long userId) {
        //First check if exists a balance for this user
        //var type is deduced by compiler, is equivalen to Optional<Balance> existingBalance (Java 10+)
        var existingBalance = balanceRepository.findByUserId(userId);
        if (existingBalance.isPresent()) {
            //If exists, we return the existent one
            return convertToDTO(existingBalance.get());
        }
        Balance balance = createInitialBalance(userId);
        return convertToDTO(balance);
    }

    public BalanceDTO updateBalanceAutomatically(Long userId) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> createInitialBalance(userId));

        //To-do: logic for update
        return convertToDTO(balance);
    }

    //Search the users balance or creates one if don't exist
    public BalanceDTO getBalanceByUserId(Long userId) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseGet(() -> createInitialBalance(userId));
        return convertToDTO(balance);
    }

    private BalanceDTO convertToDTO(Balance balance) {
        BalanceDTO dto = new BalanceDTO();
        dto.setId(balance.getId());
        dto.setUserId(balance.getUserId());
        dto.setTotalBalance(balance.getTotalBalance());
        return dto;
    }
}
