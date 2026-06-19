package com.mvm.auth.service;

import com.mvm.auth.model.User;
import com.mvm.auth.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final String transactionServiceUrl;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RestTemplate restTemplate,
                       @org.springframework.beans.factory.annotation.Value("${transaction.service.url}") String transactionServiceUrl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.transactionServiceUrl = transactionServiceUrl;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        try {
            createInitialBalanceInTransactionService(savedUser.getId());
        } catch (Exception e) {
            log.warn("Initial balance creation queued for user: {}. Will be created on first balance access.", savedUser.getId());
        }

        return savedUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @CircuitBreaker(name = "transactionService", fallbackMethod = "initialBalanceFallback")
    @Retry(name = "transactionService")
    public void createInitialBalanceInTransactionService(Long userId) {
        String url = transactionServiceUrl + "/api/balance/initial-create";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", userId);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Initial balance created for user: {}", userId);
        } else {
            log.error("Error creating initial balance for user: {}", userId);
        }
    }

    private void initialBalanceFallback(Long userId, Throwable t) {
        log.warn("Fallback: initial balance creation deferred for user: {}. Error: {}", userId, t.getMessage());
    }
}