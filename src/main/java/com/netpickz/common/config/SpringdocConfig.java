package com.netpickz.common.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

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
	
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth") // 그룹 이름
                .pathsToMatch("/auth/**") // 이 경로에 해당하는 API만 포함
                .build();
    }

    @Bean
    public GroupedOpenApi otherApi() {
        return GroupedOpenApi.builder()
                .group("v1") // 그룹 이름
                .pathsToExclude("/auth/**")
                .addOpenApiCustomizer(open -> {
                    open.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                    open.getComponents()
                        .addSecuritySchemes("bearerAuth",
                            new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        );
                })
                .build()
                ;
    }


}
