package com.mvm.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvm.auth.dto.AuthRequestDTO;
import com.mvm.auth.dto.RegisterRequestDTO;
import com.mvm.auth.model.User;
import com.mvm.auth.service.JwtService;
import com.mvm.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AuthController authController;

    //Simulates http request
    private MockMvc mockMvc;
    //Convert java objects - json
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        //Configure MockMvc with controller we want test
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_WithValidData_ShouldReturnJwtTokenAndUserInfo() throws Exception {
        //---Arrange---
        //Create valid register data
        RegisterRequestDTO request = new RegisterRequestDTO("test@test.com", "password123", "newUser");

        //Creates a User (entity), like the return of userService.registerUser()
        User fakeUser = new User();
        fakeUser.setId(1L);
        fakeUser.setEmail("test@test.com");
        fakeUser.setUserName("newUser");

        when(userService.registerUser(any(User.class))).thenReturn(fakeUser);

        //---Act---
        //Simulate HTTP post
        mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                //---Assert---
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.userName").value("newUser"))
                .andExpect(jsonPath("$.token").doesNotExist()); //Token come on headers, not body

        verify(userService).registerUser(any(User.class));
        verify(userService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokenAndUserInfo() throws Exception {
        //Valid credentials
        AuthRequestDTO request = new AuthRequestDTO("test2@test.com", "password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("test2@test.com");
        user.setUserName("userTest");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User("test2@test.com", "password", Collections.emptyList());

        //Configure Authentication mock
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(userService.findByEmail("test2@test.com")).thenReturn(user);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token-login");

        mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test2@test.com"))
                .andExpect(jsonPath("$.userName").value("userTest"))
                .andExpect(jsonPath("$.token").doesNotExist()) //Token not on body
                //Token on header
                .andExpect(header().string("Authorization", "Bearer jwt-token-login"))
                .andExpect(header().string("Access-Control-Expose-Headers", "Authorization"));

        verify(authenticationManager).authenticate(any());
        verify(userService).findByEmail("test2@test.com");
        verify(jwtService).generateToken(userDetails);
    }

}