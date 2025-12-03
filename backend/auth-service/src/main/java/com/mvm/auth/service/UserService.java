package com.mvm.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvm.auth.model.User;
import com.mvm.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper; //Jackson

    @Value("${transaction.service.url}")
    private String transactionServiceUrl;

    //Load user for spring security,which calls this method automatically during the authentication process
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        //converts our User to UserDetails
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
        //Encrypt password before save
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        createInitialBalanceInTransactionService(savedUser.getId());

        return savedUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    private void createInitialBalanceInTransactionService(Long userId){
        try{
            String url = transactionServiceUrl + "/api/balance/initial-create"; //Full url to endpoint

            //Headers for http request
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("userId", userId);
            //Convert the map to JSON
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers); //Build http entity

            //Send the post to transaction-service microservice, restTemplate allow us to specify method, url, and answer type
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class //Answer type
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Initial balance was created for user: " + userId);
            } else {
                System.err.println("Error on initial balance creation for user: " + userId);
            }

        }catch(Exception e){
            System.err.println("Error on creation of initial balance: " + e.getMessage());
        }
    }
}
