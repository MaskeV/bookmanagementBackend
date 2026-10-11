package com.apmosys.bookmgmt.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.apmosys.bookmgmt.entity.AppUser;
import com.apmosys.bookmgmt.repository.UserRepository;

import co.elastic.clients.elasticsearch.watcher.Email;
import jdk.javadoc.doclet.Reporter;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;
	

	public CustomUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String email) {
		// TODO Auto-generated method stub
		AppUser appUser = userRepository.findByEmail(email).orElse(null);
	    return User
	            .withUsername(appUser.getEmail())
	            .password(appUser.getPassword())
	            .roles(appUser.getRole().name())  
	            .build();
	}

}
