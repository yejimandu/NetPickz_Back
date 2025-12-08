package com.netpickz.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.netpickz.common.jwt.CustomLogoutFilter;
import com.netpickz.common.jwt.JWTFilter;
import com.netpickz.common.jwt.JWTUtil;
import com.netpickz.common.jwt.LoginFilter;
import com.netpickz.core.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final CustomLogoutFilter customLogoutFilter;
    private final JWTUtil jwtUtil;
    private final AuthService authService;

	//AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    
	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http , @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource, AuthenticationConfiguration authenticationConfiguration) throws Exception{
		
		http
			.csrf( auth -> auth.disable()) 		
			.cors((cors) -> cors.configurationSource(corsConfigurationSource))	
//			.cors().and().csrf().disable()
			.formLogin((auth) -> auth.disable())								// 폼 로그인 disable
			.httpBasic((auth) -> auth.disable())								// http basic 인증 방식 disable
			.authorizeHttpRequests((auth) -> auth								// 경로별 인가 작업
                .requestMatchers("/auth/**", "/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/users").permitAll()
                .requestMatchers("/movies/**", "/mail/**").permitAll()  // TODO 추후에 패스 조절 필요
                .anyRequest().authenticated())
//			.exceptionHandling(null)
			.exceptionHandling
			.addFilterBefore(jwtFilter, LoginFilter.class)
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, authService), UsernamePasswordAuthenticationFilter.class)
			.addFilterAt(customLogoutFilter, LogoutFilter.class)
			// 세션 설정 - JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정하는 것이 중요
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
			;
		return http.build();
	}
	
}
