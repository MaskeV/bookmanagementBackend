package com.apmosys.bookmgmt.filter;

import java.io.IOException;

import org.hibernate.annotations.Filter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.apmosys.bookmgmt.service.CustomUserDetailsService;
import com.apmosys.bookmgmt.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	
	private final JwtService jwtService;
	private final CustomUserDetailsService customUserDetailsService;
	
	
	
	public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
		super();
		this.jwtService = jwtService;
		this.customUserDetailsService = customUserDetailsService;
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		  String header = request.getHeader("Authorization");
	        if (header != null && header.startsWith("Bearer ")) {
	            String token = header.substring(7);
	            try {
	                String username = jwtService.extractUsername(token);
	                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	                    UserDetails user = customUserDetailsService.loadUserByUsername(username);
	                    if (jwtService.isValid(token, user)) {
	                        var auth = new UsernamePasswordAuthenticationToken(
	                                user, null, user.getAuthorities());
	                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                        SecurityContextHolder.getContext().setAuthentication(auth);
	                    }
	                }
	            } catch (JwtException | UsernameNotFoundException e) {
	                // bad or expired token: leave unauthenticated, Spring returns 401/403
	            }
	        }
	        filterChain.doFilter(request, response);
		
	}

}
