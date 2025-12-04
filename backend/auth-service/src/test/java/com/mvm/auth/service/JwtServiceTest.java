package com.mvm.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService jwtService;
    @Mock
    private UserService userService;
    private UserDetails userDetails;
    private com.mvm.auth.model.User authUser;

    @BeforeEach
    void setUp() {
        //Jwt use @Value to inject properties, on test we use ReflectionTestUtils to emulate that injection
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "userService", userService);
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "c2VjcmV0S2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);

        //Creates UserDetails for Spring Security
        userDetails = new User("test@example.com", "password", Collections.emptyList());

        authUser = new com.mvm.auth.model.User();
        authUser.setId(1L);
        authUser.setEmail("test@example.com");
        authUser.setUserName("testuser");
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        when(userService.findByEmail("test@example.com")).thenReturn(authUser);

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("test@example.com", extractedUsername);
        verify(userService).findByEmail("test@example.com");
    }

    @Test
    void extractUsername_ShouldReturnEmailFromToken() {
        when(userService.findByEmail("test@example.com")).thenReturn(authUser);
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("test@example.com", extractedUsername);
    }


    @Test
    void isTokenValid_ShouldReturnTrue_ForValidTokenAndCorrectUser() {
        when(userService.findByEmail("test@example.com")).thenReturn(authUser);
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

}