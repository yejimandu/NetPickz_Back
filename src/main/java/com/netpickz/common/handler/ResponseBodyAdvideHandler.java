package com.netpickz.common.handler;

import java.time.LocalDateTime;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netpickz.common.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;
// 1. ResponseBodyAdvideHandler 구현
// 2. 반환 타입 클래스 구현 > apiResponse
// 3. swagger 사용시 swagger 관련 요청은 무시하도록 처리
// 4. 컨트롤러 쪽에서 dto만 반환하도록 처리.
@Slf4j
@RestControllerAdvice
public class ResponseBodyAdvideHandler implements ResponseBodyAdvice<Object>{
	
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
	   // OpenApiResource(Swagger) 제외
		return !returnType.getDeclaringClass().getName().contains("OpenApiResource");
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		log.debug("ResponseBodyAdvideHandler 호출됨. path={}, body={}", request.getURI().getPath(), body.toString());
	    var path = request.getURI().getPath();
	    if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
	        return body; // Swagger 관련 요청은 그대로 반환
	    }

		 if (body instanceof ApiResponse) {
            return body;
	     }
		 
		 var apiResponse = ApiResponse.builder()
					.success(true)
					.timeStamp(LocalDateTime.now())
					.data(body)
					.status(HttpStatus.OK.value())
					.build();
		 
		 if(body instanceof String) {
			 response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		    try {
		    	var mapper = new ObjectMapper();
			    mapper.registerModule(new JavaTimeModule()); // 이거 추가!
			    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 
			    return mapper.writeValueAsString(apiResponse);
			} catch (JsonProcessingException e) {
				log.error("Fail to Json Write. apiResponse={}", apiResponse);
				throw new RuntimeException("JSON 변환 실패", e);
			}
		 }
		 log.debug("ResponseBodyAdvideHandler beforeBodyWrite apiResponse : ", apiResponse.toString());
		return apiResponse;
	}

}
