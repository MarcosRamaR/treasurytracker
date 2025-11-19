package com.mrr.treasury_tracker.service;

import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock //This creates false object
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder; //Not real encoder

    @InjectMocks
    private UserService userService; //Real UserService but false dependencies

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test2@test.com");
        testUser.setPassword("encodedPassword");
        testUser.setUserName("testuser");
    }

    //Test login user existent user by email
    @Test
    void loadUserByUserNameWithExistingEmail() {
        //Arrange: Mock behavior configuration
        //When call findByEmail with this email, then return this test user
        when(userRepository.findByEmail("test2@test.com")).thenReturn(Optional.of(testUser));

        //Run: Execute the real method
        UserDetails userDetails = userService.loadUserByUsername("test2@test.com");

        //Verify
        assertNotNull(userDetails, "UserDetails cant be null");
        assertEquals("test2@test.com", userDetails.getUsername(), "UserName must be the email");
        assertEquals("encodedPassword", userDetails.getPassword(), "Password must be the encoded one");
        verify(userRepository).findByEmail("test2@test.com"); //Check that repository was called one time
    }

    //Test login user with user not existent
    @Test
    void loadUserByUsernameWithNonExistingEmail() {
        //Arrange: configuration mock to return void
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        //Run and verify right exception
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@test.com");
        });

        //Repository always should be called
        verify(userRepository).findByEmail("nonexistent@test.com");
    }

    //Register New user successfully
    @Test
    void registerNewUser() {
        //Arrange: New user
        User newUser = new User();
        newUser.setEmail("newUser@test.com");
        newUser.setPassword("plainPassword");
        newUser.setUserName("newuser");

        //Mocks configuration
        when(userRepository.existsByEmail("newUser@test.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(2L);
            return userToSave;
        });

        //Run: save the user
        User savedUser = userService.registerUser(newUser);

        //Verify
        assertNotNull(savedUser, "Saved user cant be null");
        assertEquals("encodedPassword", savedUser.getPassword(),
                "Saved password must be encrypted");
        verify(userRepository).existsByEmail("newUser@test.com");
        verify(passwordEncoder).encode("plainPassword");
    }

    //Try register user with existent email
    @Test
    void registerUserWithExistingEmail() {
        User existingUser = new User();
        existingUser.setEmail("existing@test.com");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerUser(existingUser));

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class)); //User cant be saved, verify .save never was called
        verify(passwordEncoder, never()).encode(anyString());
    }

    //Search user by email independent of login (success)
    @Test
    void findByEmailWithValidEmail() {
        when(userRepository.findByEmail("test2@test.com")).thenReturn(Optional.of(testUser));

        User foundUser = userService.findByEmail("test2@test.com");

        assertEquals("test2@test.com", foundUser.getEmail());
        assertEquals("testuser", foundUser.getUserName());
    }

    //Search user by email independent of login (failure)
    @Test
    void findByEmailWithNoExistentEmail() {
        when(userRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findByEmail("wrong@test.com");
        });
    }
}
