package com.mvm.transaction.controller;

import com.mvm.transaction.dto.BalanceDTO;
import com.mvm.transaction.service.BalanceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping("/initial-create")
    public ResponseEntity<BalanceDTO> createInitialBalance(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        BalanceDTO balance = balanceService.createInitialBalanceForUser(userId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping
    public ResponseEntity<BalanceDTO> getBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/update-manual")
    public ResponseEntity<BalanceDTO> updateManualBalance(
            @RequestBody Map<String, BigDecimal> request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        BigDecimal newBalance = request.get("totalBalance");
        if (newBalance == null) {
            return ResponseEntity.badRequest().build();
        }
        BalanceDTO balance = balanceService.updateBalanceManual(userId, newBalance);
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/auto")
    public ResponseEntity<BalanceDTO> updateAutomaticBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.updateBalanceAutomatically(userId);
        return ResponseEntity.ok(balance);
    }
}