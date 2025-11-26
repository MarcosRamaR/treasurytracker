package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.dto.BalanceDTO;
import com.mrr.treasury_tracker.model.Balance;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.service.BalanceService;
import com.mrr.treasury_tracker.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/balance")
public class BalanceController {

    private final BalanceService balanceService;
    private final UserService userService;


    public BalanceController(BalanceService balanceService, UserService userService) {
        this.balanceService = balanceService;
        this.userService = userService;
    }

    private User getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @GetMapping
    public ResponseEntity<?> getBalance(Authentication authentication){
        User user = getCurrentUser(authentication);
        Balance balance = balanceService.getBalance(user);
        if(balance == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Balance not found");
        }
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/manual")
    public ResponseEntity<?> updateBalanceManual(@RequestBody BalanceDTO balanceDTO, Authentication authentication){
        User user = getCurrentUser(authentication);
        Balance updatedBalance = balanceService.updateBalanceManual(user, balanceDTO.getAmount());

        if(updatedBalance== null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Balance not found");
        }

        return ResponseEntity.ok(updatedBalance);
    }
    @PutMapping("/auto")
    public ResponseEntity<?> updatedBalanceAutomatic(Authentication authentication){
        User user = getCurrentUser(authentication);
        Balance updatedBalance = balanceService.updateBalanceAutomatically(user);
        if(updatedBalance== null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Balance not found");
        }
        return ResponseEntity.ok(updatedBalance);
    }

}
