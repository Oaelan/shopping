package com.example.demo.provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${secret-key}")
    private String secretKey;

    public String create(String userEmail) {
        Date expireDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String jwt = Jwts.builder() // "bulider" -> "builder"로 수정
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .compact();

        return jwt; // JWT 문자열을 반환
    }
    
    public String validate(String jwt) {
    	
    	String subject = null;
    	
    	Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    	
    	try {
    		Claims claims = Jwts.parserBuilder()
    				.setSigningKey(key)
    				.build()
    				.parseClaimsJws(jwt)
    				.getBody();
    		
    		subject = claims.getSubject();
    		
    	}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	
    	return "";

    }
}
