package com.mvm.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private CorsConfig corsConfig;
    @Autowired
    private PasswordConfig passwordEncoder;

    //This defines the security behavior for the application
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))//Cors config, allows request from frontend
                .csrf(csrf -> csrf.disable())//Disable CSRF
                //Routes authorization
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()//Public routes
                        .anyRequest().authenticated()//All other requires authentication
                )
                //Config stateless sessions (we use JWT, not http sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider()) //Custom authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //validate jwt before

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); //How load users
        authProvider.setPasswordEncoder(passwordEncoder.passwordEncoder()); //How validates passwords
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}