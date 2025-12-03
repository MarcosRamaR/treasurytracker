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

    @PostMapping("/update")
    public ResponseEntity<BalanceDTO> updateBalance(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        BalanceDTO balance = balanceService.updateBalanceAutomatically(userId);
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
