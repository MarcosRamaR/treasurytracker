package com.mrr.treasury_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean //Bean to personalice the configuration web on Spring
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) { //CorsRegistry is an object to register CORS rules
                registry.addMapping("/**") //Apply CORS to all rutes
                        .allowedOrigins("http://localhost:5173") //Allow request only from this
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") //Allow this methods
                        .allowedHeaders("*") //All headers allowed
                        .allowCredentials(true); //Allow to send credentials, important for JWT to send Authorization header
            }
        };
    }
}
