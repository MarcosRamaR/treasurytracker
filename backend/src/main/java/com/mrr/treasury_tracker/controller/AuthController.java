package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.dto.AuthRequestDTO;
import com.mrr.treasury_tracker.dto.RegisterRequestDTO;
import com.mrr.treasury_tracker.dto.UserResponseDTO;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.service.UserService;
import com.mrr.treasury_tracker.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager; //Receive credentials, search user and verify password
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    //Handle validation errors
    private ResponseEntity<Map<String, String>> getValidationErrors(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> //Get list with all fields with error and iterates over each error
                errors.put(error.getField(), error.getDefaultMessage())); //Get field name and message
        return ResponseEntity.badRequest().body(errors); //Create a Http response with 400 status
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request, BindingResult bindingResult) {
        //@Valid ENABLES automatic data validation, Spring automatically checks the annotations in the DTO class
        //If are error they will be stored on BindingResult

        if (bindingResult.hasErrors()) {//Verify validation error
            return getValidationErrors(bindingResult); //Return the errros as JSON
        }

        try {
            //Create new user and set the data from DTO to entity
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setUserName(request.getUserName());

            User savedUser = userService.registerUser(user);

            //Conversion, spring work with UserDetails and jwt wait for a UserDetail
            UserDetails userDetails = userService.loadUserByUsername(savedUser.getEmail());
            String jwt = jwtService.generateToken(userDetails);

            //Build the responseDTO with token (jwt)
            UserResponseDTO response = new UserResponseDTO(
                    jwt,
                    savedUser.getEmail(),
                    savedUser.getUserName()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getValidationErrors(bindingResult);
        }

        try {
            authenticationManager.authenticate( //Search the user by email on database, get the password and compare with storage using BCrypt
                    //Create an object of user credentials
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            //Token generation
            User user = userService.findByEmail(request.getEmail());
            UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
            String jwt = jwtService.generateToken(userDetails);

            UserResponseDTO response = new UserResponseDTO(
                    jwt,
                    user.getEmail(),
                    user.getUserName()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(error);
        }
    }
}