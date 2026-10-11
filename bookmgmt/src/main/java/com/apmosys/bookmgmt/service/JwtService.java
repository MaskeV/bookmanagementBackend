package com.apmosys.bookmgmt.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore.SecretKeyEntry;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
	
	private final SecretKey key;
	private final long expirationMs;
	
	
	public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
		super();
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));;
		this.expirationMs = expirationMs;
	}
	
	public String generateToken(String username, String role) {
		return Jwts.builder().subject(username).claim("role", role).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(key).compact();
	}
	
	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();		 
	}
	
	public boolean isValid(String token, UserDetails user) {
		try {
			return extractUsername(token).equals(user.getUsername());
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
}
