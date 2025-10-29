package com.mrr.treasury_tracker.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service //Set this as a spring service
public class JwtService {

    @Value("${jwt.secret}") //This @Value injects this value from application.properties
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    //Convert "secretKey" from Base64 to Key for JWT
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); //converts the Base64 String into a byte array
        return Keys.hmacShaKeyFor(keyBytes); //Creates cryptographic key for HMAC-SHA algorithm
    }
    //Parse and validate the token, returning all data (claims)
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() //prepare the JWT parser
                .setSigningKey(getSignInKey()) //Set the key used to verify
                .build()
                .parseClaimsJws(token) //Parses and validates the signature
                .getBody(); //Extract the token content
    }

    //Obtain any data from the token, use T to make it generic
    //Function<Claims,T> function to extract a specific data from Claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims); //Executes the function on claims
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); //Obtain data expiration and compare if is earlier than current date
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder() //Start token building
                .setClaims(extraClaims) //Extra data
                .setSubject(userDetails.getUsername()) //Main identifier
                .setIssuedAt(new Date(System.currentTimeMillis())) //Creation date
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) //Expiration date
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //Sign the token with secret key
                .compact(); //Generates the string at the end
    }

    //Validates the token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); //Extract the token user
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token); //compares token user with auth user
    }
}

