package com.mvm.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) //Activate Mockito for tests
public class JwtServiceTest {
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach //Method run before each test
    void setUp(){
        jwtService = new JwtService(); //Real instance of JwtService

        //ReflectionTestUtils to inject values for test, due to JwtService use @Value
        ReflectionTestUtils.setField(jwtService, "secretKey", "c2VjcmV0S2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=");
        ReflectionTestUtils.setField(jwtService,"jwtExpiration",86400000L);

        //Create test user for Spring Security
        userDetails = new User("test1@test.com","password", Collections.emptyList());
    }

    @Test
    void generateTokenValidAndNotNull(){
        //Run -> Call method we want test
        String token = jwtService.generateToken(userDetails);

        //Verify -> Check results
        assertNotNull(token, "Generated token must not be null");
        String[] tokenParts = token.split("\\.");
        assertEquals(3,tokenParts.length, "JWT token must be 3 parts separated by periods");
    }

    @Test
    void extractUserNameReturnMail(){
        //Arrange -> First generate token
        String token = jwtService.generateToken(userDetails);

        //Run -> Extract username's token
        String extractedUserName = jwtService.extractUsername(token);

        //Verify -> Check results
        assertEquals("test1@test.com", extractedUserName, "Exctacted email should be equals to original");
    }

    @Test
    void checkTokenIsValid() {
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid, "Un token válido con el usuario correcto debería retornar true");
    }

    @Test
    void tokenFromWrongUser() {
        //Arrange -> Generate token and wrong user
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = new User("testWrongUser@example.com", "password", Collections.emptyList());

        //Run
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        //Verify
        assertFalse(isValid, "A user token cant be valid to another user");
    }
}
