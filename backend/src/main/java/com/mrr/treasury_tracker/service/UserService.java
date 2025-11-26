package com.mrr.treasury_tracker.service;

import com.mrr.treasury_tracker.model.Balance;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.BalanceRepository;
import com.mrr.treasury_tracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService { //UserDetailsService is interface from Spring Security to load user data
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BalanceRepository balanceRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, BalanceRepository balanceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.balanceRepository = balanceRepository;
    }

    //Search a user by email and convert into right format for Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        //This looks on database, if not found send exception
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found" + email));

        //Return our user converted into User format for Spring Security, and use fully qualified name to avoid conflicts
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), //Spring use this as "username"
                user.getPassword(),
                Collections.emptyList() //List of permissions
        );
    }

    public User registerUser(User user){
        //Avoid duplicated emails
        if (userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        //Encrypt password before save user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        Balance balance = new Balance(
                BigDecimal.ZERO,
                savedUser,
                LocalDateTime.now(),
                "SYSTEM"
        );
        balanceRepository.save(balance);
        return savedUser;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

}
