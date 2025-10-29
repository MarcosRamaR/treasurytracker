package com.mrr.treasury_tracker.service;

import com.mrr.treasury_tracker.model.User;
import com.mrr.treasury_tracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService { //UserDetailsService is interface from Spring Security to load user data
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Search a user by email and convert into right format for Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        //This looks on database, if not found send exception
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found" + email));

        //Return our user converted into User format for Spring Security, and use fully qualified name to avoid conflicts
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), //Spring use this as "username"
                user.getPassword(),
                Collections.emptyList() //List of permissions
        );
    }

    

}
