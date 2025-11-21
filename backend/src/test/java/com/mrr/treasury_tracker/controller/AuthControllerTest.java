package com.mrr.treasury_tracker.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrr.treasury_tracker.dto.AuthRequestDTO;
import com.mrr.treasury_tracker.dto.RegisterRequestDTO;
import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.service.JwtService;
import com.mrr.treasury_tracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
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
    @Mock //Create false object
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks //Inject mocks on class to test
    private AuthController authController;

    private MockMvc mockMvc; //Emulate HTTP request
    private ObjectMapper objectMapper; //To convert Java -> JSON

    @BeforeEach
    void setUp(){
        //New ObjectMappjer object
        objectMapper = new ObjectMapper();
        //Configuration MockMvc with controller we want test
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    //Successfully register
    @Test
    void registerWithValidData_returnJwtTokenAndUserInfo() throws Exception{
        //Arrange

        //Create data
        RegisterRequestDTO request = new RegisterRequestDTO(
                "test3@test.com",
                "password123",
                "newUser"
        );
        //Creates simulated user
        User usuarioSimulado = new User();
        usuarioSimulado.setEmail("test3@test.com");
        usuarioSimulado.setUserName("newUser");

        //Configuration mocks behavior: when(mock.method()).thenReturn(value)
        when(userService.registerUser(any(User.class))).thenReturn(usuarioSimulado);
        when(userService.loadUserByUsername("test3@test.com"))
                .thenReturn(new org.springframework.security.core.userdetails
                        .User("test3@test.com", "password123", Collections.emptyList()));
        when(jwtService.generateToken(any(UserDetails.class)))
                .thenReturn("fake-jwt-token");

        //Act and Assert

        //Simulate a http request for post method
        mockMvc.perform(
                post("/api/auth/register")//URL and request type
                        .contentType(MediaType.APPLICATION_JSON) //Header
                        .content(objectMapper.writeValueAsString(request)) //Body on JSON
        )
        .andExpect(status().isOk()) //we look for status 200
        .andExpect(jsonPath("$.token").value("fake-jwt-token"))//Check right token
        .andExpect(jsonPath("$.email").value("test3@test.com"))//Check right email
        .andExpect(jsonPath("$.userName").value("newUser"));//Check username

        //Verify we call this methods
        verify(userService).registerUser(any(User.class));
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    //Wrong register (invalid data)
    @Test
    void registerWithInvalidData() throws Exception {
        //We want to use a string instead of a java object to avoid the verification on object creation
        String invalidJsonRequest = """
            {
                "email": "not-an-email",
                "password": "123",
                "userName": "ab"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonRequest))
                .andExpect(status().isBadRequest()) //Status http must be 400, not 200 ok
                .andExpect(jsonPath("$.email").exists()) //Must exist an error to email field
                .andExpect(jsonPath("$.password").exists()) //Must exist an error to password field
                .andExpect(jsonPath("$.userName").exists()); //Must exist an error to userName field

        verify(userService, never()).registerUser(any(User.class));
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    //Register with an existing email
    @Test
    void whenRegisterExistingEmail_thenReturnError() throws Exception {

        //Create a right format data
        RegisterRequestDTO request = new RegisterRequestDTO(
                "test3@test.com",
                "password123",
                "userTest"
        );

        //Configuration mock to throw an exception when try save a existent user
        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest()) //Status 400
                .andExpect(jsonPath("$.error").value("Email already exists")); //Verify mention on email already exists
    }

    //Sucessfully login
    @Test
    void loginWithValidCredentials() throws Exception{
        //Creates valid login data
        AuthRequestDTO request = new AuthRequestDTO("test3@test.com", "password123");

        //Creates a simulated user
        User user = new User();
        user.setEmail("test3@test.com");
        user.setUserName("userTest");

        when(userService.findByEmail("test3@test.com")).thenReturn(user);
        when(userService.loadUserByUsername("test3@test.com")).thenReturn(
                new org.springframework.security.core.userdetails.User(
                        "test3@test.com", "password", Collections.emptyList()));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token-login");

        //Simulated post http request
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-login"))
                .andExpect(jsonPath("$.email").value("test3@test.com"))
                .andExpect(jsonPath("$.userName").value("userTest"));

    }


}
