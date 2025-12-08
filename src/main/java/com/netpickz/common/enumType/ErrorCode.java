package com.netpickz.common.enumType;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	TOKEN_MISSING("TOKEN_001", "토큰이 제공되지 않았습니다.", HttpStatus.UNAUTHORIZED),
	
	// ACCESS_TOKEN
	ACCESS_TOKEN_EXPIRED("TOKEN_101", "액세스 토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
	ACCESS_TOKEN_INVALID("TOKEN_102", "액세스 토큰이 유효하지않습니다.", HttpStatus.UNAUTHORIZED),
	ACCESS_TOKEN_MALFORMED("TOKEN_103", "액세스 토큰 형식이 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
	ACCESS_TOKEN_UNSUPPORTED("TOKEN_104", "지원하지 않는 액세스 토큰 형식입니다", HttpStatus.UNAUTHORIZED),
	
	// REFRESH_TOKEN
	REFRESH_TOKEN_EXPIRED("TOKEN_201", "리프레쉬 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED ),
	REFRESH_TOKEN_INVALID("TOKEN_202", "유효하지 않은 리프레쉬 토큰입니다.", HttpStatus.UNAUTHORIZED ),
	REFRESH_TOKEN_MALFORMED("TOKEN_203", "리프레쉬 토큰 형식이 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
	REFRESH_TOKEN_UNSUPPORTED("TOKEN_204", "지원하지 않는 리프레쉬 토큰 형식입니다", HttpStatus.UNAUTHORIZED),
	REFRESH_TOKEN_NULL("TOKEN_205", "리프레쉬 토큰이 빈 값입니다.", HttpStatus.UNAUTHORIZED),
	
	MAIL_SEND_FAIL("MAIL_001", "메일 발송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	MAIL_INVALID("MAIL_002", "잘못된 메일 주소입니다.", HttpStatus.BAD_REQUEST),
	
	REDIS_CONNECT_FAIL("REDIS_001", "레디스 연결에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
	
//	TMDB_
	
	
	;
	
	
	private final String code;
	private final String msg;
	private final HttpStatus status;
}
