package com.apmosys.bookmgmt.configuration;


import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.apmosys.bookmgmt.entity.AppUser;
import com.apmosys.bookmgmt.entity.Role;
import com.apmosys.bookmgmt.filter.JwtAuthFilter;
import com.apmosys.bookmgmt.repository.UserRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) {
		 http
         .csrf(csrf -> csrf.disable())                     
         .cors(Customizer.withDefaults())
         .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
         .authorizeHttpRequests(auth -> auth
             .requestMatchers("/api/auth/**").permitAll()
             .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
             .requestMatchers("/api/admin/**").hasRole("ADMIN")
             .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("USER", "ADMIN")
             .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
             .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
             .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
             .anyRequest().authenticated())
         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
       return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173","http://localhost:4200"));
	    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
	    configuration.setAllowedHeaders(List.of("*"));
	    configuration.setMaxAge(3600L);   // browser caches the preflight for 1 hour

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
	
	@Bean
	CommandLineRunner seedAdmin(UserRepository repo, PasswordEncoder encoder) {
	    return args -> {
	        if (!repo.existsByEmail("admin@example.com")) {
	            AppUser a = new AppUser();
	            a.setName("admin");
	            a.setEmail("admin@example.com");
	            a.setPassword(encoder.encode("Admin@123"));   // change later
	            a.setRole(Role.ADMIN);
	            repo.save(a);
	        }
	    };
	}
	

}