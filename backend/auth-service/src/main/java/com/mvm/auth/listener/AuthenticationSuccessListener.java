package com.mvm.auth.listener;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

//Listens when user succesfully autenticates
@Component
public class AuthenticationSuccessListener {

    /*
    * TO-DO: Return to this method the automatic creation of the balance (or valorate put it on registration)
    * */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        System.out.println("User authenticated successfully: " + username);
    }
}
