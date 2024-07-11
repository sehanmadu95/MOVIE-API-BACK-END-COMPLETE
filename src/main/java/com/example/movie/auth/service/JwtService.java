package com.example.movie.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

   // base 64 256
   private static final String SECRET_KEY="67f40e2c3c00054a644d854e487089446a0ac4ce399e5c8aefdc7858305ebc6b";

    //extract username from JWT
    public String extractUserName(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //extract information from JWT
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //decode and get the jkey
    private Key getSignInKey(){
        //decode secret key;
        byte [] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    //Generate token usng jwt utility class and return token as String
    public String generateToken(Map<String, Object>extraClaims,UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+25*1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //if token is valid by checking if token is expired for current user
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String usserName=extractUserName(token);
        return (usserName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //if token is expired
    private boolean isTokenExpired(String token){
        return extractExpired(token).before(new Date());
    }

    private Date extractExpired(String token){
        return extractClaim(token,Claims::getExpiration);
    }
}
