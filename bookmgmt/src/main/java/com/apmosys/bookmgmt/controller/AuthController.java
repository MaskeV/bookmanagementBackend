package com.apmosys.bookmgmt.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apmosys.bookmgmt.dto.AuthResponse;
import com.apmosys.bookmgmt.dto.LoginRequest;
import com.apmosys.bookmgmt.dto.RegisterRequest;
import com.apmosys.bookmgmt.entity.AppUser;
import com.apmosys.bookmgmt.entity.Role;
import com.apmosys.bookmgmt.repository.UserRepository;
import com.apmosys.bookmgmt.service.JwtService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final UserRepository repo;
	private final PasswordEncoder encoder;
	private final AuthenticationManager authManager;
	private final JwtService jwtService;
	
	
	public AuthController(UserRepository repo, PasswordEncoder encoder, AuthenticationManager authManager,
			JwtService jwtService) {
		super();
		this.repo = repo;
		this.encoder = encoder;
		this.authManager = authManager;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
	    String email = req.email().trim().toLowerCase();
	    if (repo.existsByEmail(email))
	        return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));

	    AppUser u = new AppUser();
	    u.setName(req.name().trim());
	    u.setEmail(email);
	    u.setPassword(encoder.encode(req.password()));
	    u.setRole(Role.USER);
	    repo.save(u);
	    String token = jwtService.generateToken(u.getEmail(), u.getRole().name());
	    return ResponseEntity.ok(new AuthResponse(token, u.getName(), u.getRole().name()));
	    
 	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
	    String email = req.email().trim().toLowerCase();
	    try {
	        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.password()));
	    } catch (AuthenticationException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("message", "Invalid email or password"));
	    }
	    AppUser u = repo.findByEmail(email).orElseThrow();
	    String token = jwtService.generateToken(u.getEmail(), u.getRole().name());
	    return ResponseEntity.ok(new AuthResponse(token, u.getName(), u.getRole().name()));
	}
	

}