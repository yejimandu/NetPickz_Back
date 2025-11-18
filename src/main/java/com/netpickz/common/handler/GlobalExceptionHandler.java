package com.netpickz.common.handler;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.netpickz.common.dto.ApiResponse;

// 1. 전역 예외 처리 핸들러 클래스 생성
// 2. @ExceptionHandler할 메소드 작성 
// 3. 반환 데이터 관련으로 errorCode enum 파일 생성
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Object>> handlerException (CustomException  e) {
		var errorCode = e.getErrorCode();
        
        return ResponseEntity
        		.status(errorCode.getStatus())
        		.body(
	        		 ApiResponse.builder()
		 				.success(false)
		 				.message(errorCode.getMsg())
		 				.code(errorCode.getCode())
		 				.timeStamp(LocalDateTime.now())
		 				.status(errorCode.getStatus().value())
		 				.build()
        		);
	} 
}
