package com.netpickz.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JacksonConfig {
	
	@Bean
	public ObjectMapper objectMapper() {
		var objectmapper = new ObjectMapper();
		objectmapper.registerModule(new JavaTimeModule()); // 이거 추가!
		objectmapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 
		log.info("ObjectMapper Config: JavaTimeModule 등록, WRITE_DATES_AS_TIMESTAMPS 비활성화");
	    return objectmapper;
	}
	
}
