package com.mvm.transaction.controller;

import com.mvm.transaction.dto.BalanceDTO;
import com.mvm.transaction.service.BalanceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/balance")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @PostMapping("/initial-create")
    public ResponseEntity<BalanceDTO> createInitialBalance(@RequestBody CreateBalanceRequest request) {
        Long userId = request.getUserId();
        BalanceDTO balance = balanceService.createInitialBalanceForUser(userId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping
    public ResponseEntity<BalanceDTO> getBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/update-auto")
    public ResponseEntity<BalanceDTO> updateBalanceAutomatic(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.updateBalanceAutomatically(userId);
        return ResponseEntity.ok(balance);
    }
    @PutMapping("/update-manual")
    public ResponseEntity<BalanceDTO> updateBalanceManual(@RequestBody BalanceDTO balanceDTO,HttpServletRequest request) {
        log.info("Resquest: {}", request);
        log.info("Amount on controller: {}", balanceDTO.getTotalBalance());
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.updateBalanceManual(userId,balanceDTO.getTotalBalance());
        return ResponseEntity.ok(balance);
    }

    /*Internal class to represent the request, spring convert the body JSON on object of this class
    Spring read the json, create a instance, call setUser(), return the full object to controller method */
    public static class CreateBalanceRequest {
        private Long userId;

        //Getters and setters are necessary so that Spring can map the JSON
        public Long getUserId() {
            return userId;
        }
        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}
