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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() //Allow OPTIONS request for preflight cors
                        .anyRequest().authenticated() //All requests need authentication
                )
                .addFilterBefore(jwtValidationFilter(), UsernamePasswordAuthenticationFilter.class); //Validates if token exists

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //Allow frontend since Vite
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        //Allow the http methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        //What headers are allowed
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Cache-Control",
                "Pragma",
                "Expires"
        ));

        //Expose auth header to front can read it
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        //Cache time for preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public OncePerRequestFilter jwtValidationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    System.out.println("OPTIONS request - skipping JWT validation");
                    filterChain.doFilter(request, response);
                    return;
                }
                String authHeader = request.getHeader("Authorization"); //Get this header

                if(authHeader == null || !authHeader.startsWith("Bearer ")){
                    response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

                    //Set the authentication on security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    //Token invalid
                    response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
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
