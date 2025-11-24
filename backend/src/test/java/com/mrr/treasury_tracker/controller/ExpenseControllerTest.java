package com.mrr.treasury_tracker.controller;

import com.mrr.treasury_tracker.repository.ExpenseRepository;
import com.mrr.treasury_tracker.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;
    
    @InjectMocks
    private ExpenseController expenseController;
}
