package com.mrr.treasury_tracker.config;

import com.mrr.treasury_tracker.service.JwtService;
import com.mrr.treasury_tracker.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component //Spring manage this as bean
public class JwtAuthenticationFilter extends OncePerRequestFilter { //Execute this one time per request
    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    //Verify if the request have token JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); //Search the auth header
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        final String jwt = authHeader.substring(7); //Get token and eliminate "Bearer "
        final String userEmail = jwtService.extractUsername(jwt); //Get the email

        //Verify that token had a correct email and that user not authenticated yet on this request
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userService.loadUserByUsername(userEmail); //Get Data from this user

            //If token valid, authenticate user on Spring Security
            if (jwtService.isTokenValid(jwt, userDetails)) {

                //Create an authentication object on Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); //Set user as user authenticated on this request
            }
        }
        filterChain.doFilter(request, response); //Continue with next filter on chain
    }

}
