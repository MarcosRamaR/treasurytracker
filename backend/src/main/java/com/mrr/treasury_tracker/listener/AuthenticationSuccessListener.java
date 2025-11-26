package com.mrr.treasury_tracker.listener;

import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.service.BalanceService;
import com.mrr.treasury_tracker.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;

import static java.lang.System.*;

public class AuthenticationSuccessListener {
    private final BalanceService balanceService;
    private final UserService userService;


    public AuthenticationSuccessListener(BalanceService balanceService, UserService userService) {
        this.balanceService = balanceService;
        this.userService = userService;
    }
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        try {
            UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());

            if (user != null) {
                balanceService.updateBalanceAutomatically(user);
            }
        } catch (Exception e) {
            //Log error without interrupt login
            err.println("Error updating balance on login: " + e.getMessage());
        }
    }

}
