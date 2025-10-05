package com.netpickz.common.config;

import java.net.http.HttpClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	
	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http) throws Exception{
		
		http
			.csrf( csrf -> csrf.disable())
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/", "/login", "/users/**","/swagger-ui/**", "/v3/api-docs/**", "/mail/**").permitAll()
//				.requestMatchers("/user").hasRole("USER")
				.anyRequest().authenticated()
			)
			;
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder encode() {
		return new BCryptPasswordEncoder();
	}
}
