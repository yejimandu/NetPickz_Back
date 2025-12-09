package com.netpickz.common.handler;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.common.enumType.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		System.out.println(authException);
		
		var errorCode = ErrorCode.ACCESS_TOKEN_INVALID;
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
