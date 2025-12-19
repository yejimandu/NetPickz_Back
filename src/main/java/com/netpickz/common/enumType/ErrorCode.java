package com.netpickz.common.enumType;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	AUTH_ACCESS_DENIED("AUTH_001", "접근 권한이 제한되었습니다..", HttpStatus.FORBIDDEN),
	AUTH_GUEST_NOT_ALLOWED("AUTH_002", "게스트 계정은 허용되지 않습니다.", HttpStatus.FORBIDDEN),

	TOKEN_MISSING("TOKEN_001", "토큰이 제공되지 않았습니다.", HttpStatus.UNAUTHORIZED),
	TOKEN_INVALID("TOKEN_002", "토큰이 유효하지않습니다.", HttpStatus.UNAUTHORIZED),
	
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
	
	JSON_WRITE_FAIL("JSON_001", "JSON 응답을 작성하지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	TMDB_BAD_REQUEST("TMDB_400", "잘못된 요청입니다. 입력값을 확인해주세요.", HttpStatus.BAD_REQUEST),
	TMDB_UNAUTHORIZED("TMDB_401", "TMDB 인증에 실패했습니다. API 키를 확인해주세요.", HttpStatus.UNAUTHORIZED),
	TMDB_FORBIDDEN("TMDB_403", "TMDB 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
	TMDB_NOT_FOUND("TMDB_404", "요청한 TMDB 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	TMDB_TOO_MANY_REQUESTS("TMDB_429", "TMDB 요청 횟수가 초과되었습니다. 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS),
	TMDB_SERVER_ERROR("TMDB_500", "TMDB 서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	TMDB_BAD_GATEWAY("TMDB_502", "TMDB 게이트웨이 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
	TMDB_SERVICE_UNAVAILABLE("TMDB_503", "TMDB 서비스가 현재 이용 불가 상태입니다. 잠시 후 다시 시도해주세요.", HttpStatus.SERVICE_UNAVAILABLE),
	TMDB_GATEWAY_TIMEOUT("TMDB_504", "TMDB 서버 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT),
	
	SERVER_ERROR("SERVER_500", "요청을 처리 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	
	MOVIE_NOT_FOUND("MOVIE_404", "영화 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	
	USER_NOT_FOUND("USER_404", "사용자 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND), 
	
	SESSION_NOT_FOUND("SESSION_404", "세션 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND), 

	DATABASE_ERROR("DB_500", "DB 서버 오류가 발생했습니다..", HttpStatus.INTERNAL_SERVER_ERROR), 
	DATABASE_DUPLICATE_KEY("DB_501", "DB PK/Unique 제약 조건 위반되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR), 
	DATABASE_DATA_INTERGRITY_VIOLATION("DB_502", "DB FK 제약 조건, NOT NULL 위반 등 데이터 무결성 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR), 
	
	
	;
	
	
	private final String code;
	private final String msg;
	private final HttpStatus status;
}
