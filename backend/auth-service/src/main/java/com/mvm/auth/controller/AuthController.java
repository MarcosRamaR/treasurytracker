package com.mvm.auth.controller;

import com.mvm.auth.dto.AuthRequestDTO;
import com.mvm.auth.dto.RegisterRequestDTO;
import com.mvm.auth.dto.UserResponseDTO;
import com.mvm.auth.model.User;
import com.mvm.auth.service.JwtService;
import com.mvm.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUserName(request.getUserName());

        User registeredUser = userService.registerUser(user);
        
        UserResponseDTO response = new UserResponseDTO();
        response.setId(registeredUser.getId());
        response.setEmail(registeredUser.getEmail());
        response.setUserName(registeredUser.getUserName());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (authentication.isAuthenticated()) {
            User user = userService.findByEmail(request.getEmail());
            
            // Extraer UserDetails del Authentication para generar el token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            
            UserResponseDTO response = new UserResponseDTO();
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setUserName(user.getUserName());
            
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body(response);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
