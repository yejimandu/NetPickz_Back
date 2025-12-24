package com.netpickz.common.handler;

import java.time.LocalDateTime;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.netpickz.common.dto.ApiResponse;
import com.netpickz.common.dto.LogDTO;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.jwt.JWTUtil;
import com.netpickz.core.log.LogService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

// 1. 전역 예외 처리 핸들러 클래스 생성
// 2. @ExceptionHandler할 메소드 작성 
// 3. 반환 데이터 관련으로 errorCode enum 파일 생성
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private final LogService logService;
	private final JWTUtil jwtUtil;
	
	public GlobalExceptionHandler(LogService logService,  JWTUtil jwtUtil) {
		super();
		this.logService = logService;
		this.jwtUtil = jwtUtil;
	}
	
	// TODO JWTEXCEPTION 확인
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiResponse<Object>> handlerJwtException ( HttpServletRequest request, JwtException  e ) {
		log.debug("JwtException 발생.  msg={}", e.getMessage(), e);
		if(e instanceof ExpiredJwtException) {
			return buildResponse(request, ErrorCode.TOKEN_EXPIRED, e);
		} else if(e instanceof MalformedJwtException) {
			return buildResponse(request, ErrorCode.TOKEN_MALFORMED, e);
		} else if(e instanceof UnsupportedJwtException) {
			return buildResponse(request, ErrorCode.TOKEN_UNSUPPORTED, e);
		}
		return buildResponse(request, ErrorCode.TOKEN_INVALID, e);
	}
	
	// TODO SERVER_ERROR 이거 에러도 처리
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiResponse<Object>> handlerDataException ( HttpServletRequest request, DataAccessException  e ) {
		log.debug("DataAccessException 발생.  msg={}", e.getMessage(), e);
		if(e instanceof DuplicateKeyException) {
			return buildResponse(request, ErrorCode.DATABASE_DUPLICATE_KEY, e);
		}else if(e instanceof DataIntegrityViolationException) {
			return buildResponse(request, ErrorCode.DATABASE_DATA_INTERGRITY_VIOLATION, e);
		} 
		return buildResponse(request, ErrorCode.DATABASE_ERROR, e);
	}
	
	@ExceptionHandler(NetPickzException.class)
	public ResponseEntity<ApiResponse<Object>> handlerNetPickzException ( HttpServletRequest request, NetPickzException  e ) {
		//TODO 추가 ㅂ완 필요
		return buildResponse(request, e.getErrorCode(), e);
	}
	
	@ExceptionHandler(WebClientResponseException.class)
	public ResponseEntity<ApiResponse<Object>> handlerWebClientResponseException ( HttpServletRequest request,  WebClientResponseException  e ) {
		log.error("WebClientResponseException 발생. errorCode={}, msg={}", e.getStatusCode(), e.getMessage(), e);
		var status = e.getStatusCode();
		if(status == HttpStatus.FORBIDDEN) {
			return buildResponse(request, ErrorCode.TMDB_FORBIDDEN, e);
		}else if(status == HttpStatus.BAD_GATEWAY) {
			return buildResponse(request, ErrorCode.TMDB_BAD_GATEWAY, e);
		}else if(status == HttpStatus.BAD_REQUEST) {
			return buildResponse(request, ErrorCode.TMDB_BAD_REQUEST, e);
		}else if(status == HttpStatus.GATEWAY_TIMEOUT) {
			return buildResponse(request, ErrorCode.TMDB_GATEWAY_TIMEOUT, e);
		}else if(status == HttpStatus.UNAUTHORIZED) {
			return buildResponse(request, ErrorCode.TMDB_UNAUTHORIZED, e);
		}else if(status == HttpStatus.SERVICE_UNAVAILABLE) {
			return buildResponse(request, ErrorCode.TMDB_SERVICE_UNAVAILABLE, e);
		}else if(status == HttpStatus.NOT_FOUND) {
			return buildResponse(request, ErrorCode.TMDB_NOT_FOUND, e);
		}else if(status == HttpStatus.TOO_MANY_REQUESTS) {
			return buildResponse(request, ErrorCode.TMDB_TOO_MANY_REQUESTS, e);
		}
		return buildResponse(request, ErrorCode.TMDB_SERVER_ERROR, e);
	}

	private ResponseEntity<ApiResponse<Object>> buildResponse ( HttpServletRequest request, ErrorCode errorCode, Exception e) {
		var apiResponse = ResponseEntity
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
		log.info("GlobalExceptionHandler handlerException apiResponse : " + apiResponse.toString() );
		if(!request.getRequestURI().contains("logout") || !request.getRequestURI().contains("login")) { 
			var userId = jwtUtil.getUsername(request.getHeader("Authorization").substring(7));
			logService.save(LogDTO.builder()
					.serviceName(request.getClass().getName()) 
					.path(request.getRequestURI()) 
					.method(request.getMethod().toString()) 
					.ipAddress(request.getRemoteAddr()) 
					.stackTrace(ExceptionUtils.getStackTrace(e))
					.userAgent(request.getHeader("User-Agent"))
					.statusCode(errorCode.getStatus().value())
					.message(errorCode.getMsg())
					.userId(userId)
					.build());
		}
        return apiResponse;
	} 
}
