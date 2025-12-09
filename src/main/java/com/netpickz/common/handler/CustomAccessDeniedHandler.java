package com.netpickz.common.handler;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.common.enumType.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	 private final ObjectMapper objectMapper;
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		System.out.println("CustomAccessDeniedHandler");
		System.out.println(request);
		System.out.println(response);
		System.out.println(accessDeniedException);
		
		var errorCode = ErrorCode.AUTH_ACCESS_DENIED;
		setErrorResponse(errorCode, response);
	}

	private void setErrorResponse(ErrorCode errorCode, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    
	    var apiResponse = ApiResponse.builder()
 				.success(false)
 				.message(errorCode.getMsg())
 				.code(errorCode.getCode())
 				.timeStamp(LocalDateTime.now())
 				.status(errorCode.getStatus().value())
 				.build();
    
	    try {
	    	response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	    }catch(JsonProcessingException e) {
	    	throw new CustomException(ErrorCode.JSON_WRITE_FAIL);
	    }
	}

}
