package com.mvm.transaction.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().authenticated() //All requests need authentication
                )
                .addFilterBefore(jwtValidationFilter(), UsernamePasswordAuthenticationFilter.class); //Validates if token exists

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtValidationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization"); //Get this header

                if(authHeader == null || !authHeader.startsWith("Bearer ")){
                    filterChain.doFilter(request, response);
                    return;
                }
                String token = authHeader.substring((7));
                try {
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(getSignInKey()) //Correct key
                            .build()
                            .parseClaimsJws(token)
                            .getBody();


                    Long userId = claims.get("userId", Long.class); //Get hte userId that auth-service set on token
                    String userEmail = claims.getSubject();

                    if (userId == null|| userEmail == null) {
                        throw new RuntimeException("Token valid but without userId or email");
                    }

                    //Save userId for controllers
                    request.setAttribute("userId", userId);

                    //Creates a authentication for Spring Security
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userEmail,  // principal
                                    null,       // credentials (null because user is authenticated)
                                    new ArrayList<>()  // authorities (void without roles)
                            );
                    //Set the authentication on security context
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establece la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    //Token invalid
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token inválido: " + e.getMessage());
                    return;
                }
                //Token valid, continue
                filterChain.doFilter(request,response);
            }
        };
    }

    //Get key to validate signs from application.properties
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
