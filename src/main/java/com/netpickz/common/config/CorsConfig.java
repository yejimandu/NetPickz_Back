package com.netpickz.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class CorsConfig {

	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
		
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 허용할 Origin
//        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 허용할 Origin
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 쿠키/인증정보 포함 허용

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CorsConfig Info : Origin={}, Methods={}, Headers={}, allowCredentials={}",
        		configuration.getAllowedOrigins(), configuration.getAllowedMethods(), 
        		configuration.getAllowedHeaders(), configuration.getAllowCredentials());
        return source;
    }

}
