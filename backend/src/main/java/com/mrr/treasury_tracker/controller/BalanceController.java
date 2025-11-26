package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.dto.BalanceDTO;
import com.mrr.treasury_tracker.model.Balance;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.BalanceRepository;
import com.mrr.treasury_tracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api/balance")
public class BalanceController {
    private BalanceRepository balanceRepository;
    private UserService userService;

    private User getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @GetMapping
    public ResponseEntity<?> getBalance(Authentication authentication){
        User user = getCurrentUser(authentication);
        Optional<Balance> balance = balanceRepository.findByUserId(user.getId());
        if(balance.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Balance not found");
        }
        return ResponseEntity.ok(balance);
    }

    @PutMapping
    public ResponseEntity<?> updateBalance(@RequestBody BalanceDTO balanceDTO, Authentication authentication){
        User user = getCurrentUser(authentication);

        Balance balance = balanceRepository.findByUserId(user.getId()).orElse(null);
        if(balance== null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Balance not found");
        }
        balance.setAmount(balanceDTO.getAmount());
        balance.setUpdatedAt(LocalDateTime.now());
        Balance updatedBalance = balanceRepository.save(balance);

        return ResponseEntity.ok(updatedBalance);
    }

}
