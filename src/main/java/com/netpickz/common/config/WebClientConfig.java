package com.netpickz.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${tmdb.url}")
	String tmdbBaseUrl;
	
	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.baseUrl(tmdbBaseUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.build();
	}

}
