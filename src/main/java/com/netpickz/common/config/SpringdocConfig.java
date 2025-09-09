package com.netpickz.common.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

// 참고 
// 1. https://velog.io/@zosungwoo/Spring-Boot-Spring-Boot-3-%EC%9D%B4%ED%9B%84-Swagger-%EC%A0%81%EC%9A%A9-springdoc-not-springfox
// 2. https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration/
@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "NetPickz API 문서", 
		version = "v1.0",
		description = "AI 기반 영화 추천 플랫폼"))
public class SpringdocConfig {

}
