package com.mvm.transaction.controller;

import com.mvm.transaction.dto.BalanceDTO;
import com.mvm.transaction.service.BalanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/balance")
public class BalanceController {
    @Autowired
    private BalanceService balanceService;

    @GetMapping
    public ResponseEntity<BalanceDTO> getBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/update")
    public ResponseEntity<BalanceDTO> updateBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.updateBalanceAutomatically(userId);
        return ResponseEntity.ok(balance);
    }
}
