package com.netpickz.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.common.jwt.CustomLogoutFilter;
import com.netpickz.common.jwt.JWTFilter;
import com.netpickz.common.jwt.JWTUtil;
import com.netpickz.core.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

//	.addFilterBefore(new JWTFilter(jwtUtil, cookieUtil, authService), LoginFilter.class)
    @Bean
    public JWTFilter jwtFilter(JWTUtil jwtUtil, AuthService authService, ObjectMapper mapper ) {
        return new JWTFilter(jwtUtil, authService, mapper);
    }
    
//    .addFilterAt(new CustomLogoutFilter(jwtUtil, userTokensRepository), LogoutFilter.class)
    @Bean
    public CustomLogoutFilter customLogoutFilter(AuthService authService) {
        return new CustomLogoutFilter(authService);
    }

    
    
}
