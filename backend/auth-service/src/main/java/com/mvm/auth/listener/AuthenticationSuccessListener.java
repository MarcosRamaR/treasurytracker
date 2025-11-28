package com.mvm.auth.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        // Log de autenticación exitosa
        // La creación de balances iniciales se manejará en transaction-service
        // cuando el usuario acceda por primera vez a las funcionalidades de transacciones
        String username = event.getAuthentication().getName();
        System.out.println("User authenticated successfully: " + username);
    }
}
