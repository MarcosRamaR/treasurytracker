package com.mvm.auth.controller;

import com.mvm.auth.dto.AuthRequestDTO;
import com.mvm.auth.dto.LoginResponseDTO;
import com.mvm.auth.dto.RefreshTokenRequestDTO;
import com.mvm.auth.dto.RegisterRequestDTO;
import com.mvm.auth.dto.UserResponseDTO;
import com.mvm.auth.model.User;
import com.mvm.auth.service.JwtService;
import com.mvm.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        if (!request.getPassword().equals(request.getPassword2())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(UserResponseDTO.builder()
                            .error("Passwords do not match")
                            .build());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setUserName(request.getUserName());

        User registeredUser = userService.registerUser(user);

        UserResponseDTO response = UserResponseDTO.builder()
                .id(registeredUser.getId())
                .email(registeredUser.getEmail())
                .userName(registeredUser.getUserName())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (authentication.isAuthenticated()) {
            User user = userService.findByEmail(request.getEmail());
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            LoginResponseDTO response = LoginResponseDTO.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .userName(user.getUserName())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("X-Refresh-Token", refreshToken);
            headers.add("Access-Control-Expose-Headers", "Authorization, X-Refresh-Token");

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

        throw new RuntimeException("Invalid credentials");
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        try {
            String userEmail = jwtService.extractUsername(request.getRefreshToken());
            UserDetails userDetails = userService.loadUserByUsername(userEmail);

            if (jwtService.isRefreshTokenValid(request.getRefreshToken(), userDetails)) {
                String newAccessToken = jwtService.generateToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);

                User user = userService.findByEmail(userEmail);

                LoginResponseDTO response = LoginResponseDTO.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .userName(user.getUserName())
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + newAccessToken);
                headers.add("Access-Control-Expose-Headers", "Authorization, X-Refresh-Token");

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponseDTO.builder()
                        .error("Invalid or expired refresh token")
                        .build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findByEmail(authentication.getName());
        return ResponseEntity.ok(UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .build());
    }
}