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
	TOKEN_EXPIRED("TOKEN_003", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	TOKEN_MALFORMED("TOKEN_004", "액세스 토큰 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
	TOKEN_UNSUPPORTED("TOKEN_005", "지원하지 않는 액세스 토큰 형식입니다.", HttpStatus.UNAUTHORIZED),

	// ACCESS_TOKEN
	ACCESS_TOKEN_EXPIRED("TOKEN_101", "액세스 토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
	ACCESS_TOKEN_INVALID("TOKEN_102", "액세스 토큰이 유효하지않습니다.", HttpStatus.UNAUTHORIZED),
	ACCESS_TOKEN_MALFORMED("TOKEN_103", "액세스 토큰 형식이 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
	ACCESS_TOKEN_UNSUPPORTED("TOKEN_104", "지원하지 않는 액세스 토큰 형식입니다", HttpStatus.UNAUTHORIZED),

	// REFRESH_TOKEN
	REFRESH_TOKEN_EXPIRED("TOKEN_201", "리프레쉬 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	REFRESH_TOKEN_INVALID("TOKEN_202", "유효하지 않은 리프레쉬 토큰입니다.", HttpStatus.UNAUTHORIZED),
	REFRESH_TOKEN_MALFORMED("TOKEN_203", "리프레쉬 토큰 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
	REFRESH_TOKEN_UNSUPPORTED("TOKEN_204", "지원하지 않는 리프레쉬 토큰 형식입니다", HttpStatus.BAD_REQUEST),
	REFRESH_TOKEN_NULL("TOKEN_205", "리프레쉬 토큰이 빈 값입니다.", HttpStatus.BAD_REQUEST),

	LOGIN_FAIL("LOGIN_001", "로그인에 실패하였습니다. 아이디 비밀번호를 다시 확인 후 시도해주세요.", HttpStatus.BAD_REQUEST),

	
	MAIL_SEND_FAIL("MAIL_001", "메일 발송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	MAIL_INVALID("MAIL_002", "잘못된 메일 주소입니다.", HttpStatus.BAD_REQUEST),
	MAIL_VERIFY_FAIL("MAIL_003", "메일 인증에 실패했습니다. 인증번호를 다시 확인해주세요.", HttpStatus.BAD_REQUEST),

	REDIS_CONNECT_FAIL("REDIS_001", "레디스 연결에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

	JSON_WRITE_FAIL("JSON_001", "JSON 응답을 작성하지 못했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	TMDB_BAD_REQUEST("TMDB_001", "잘못된 요청입니다. 입력값을 확인해주세요.", HttpStatus.BAD_REQUEST),
	TMDB_UNAUTHORIZED("TMDB_002", "TMDB 인증에 실패했습니다. API 키를 확인해주세요.", HttpStatus.UNAUTHORIZED),
	TMDB_FORBIDDEN("TMDB_003", "TMDB 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
	TMDB_NOT_FOUND("TMDB_004", "요청한 TMDB 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	TMDB_TOO_MANY_REQUESTS("TMDB_005", "TMDB 요청 횟수가 초과되었습니다. 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS),
	TMDB_SERVER_ERROR("TMDB_006", "TMDB 서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	TMDB_BAD_GATEWAY("TMDB_007", "TMDB 게이트웨이 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
	TMDB_SERVICE_UNAVAILABLE("TMDB_008", "TMDB 서비스가 현재 이용 불가 상태입니다. 잠시 후 다시 시도해주세요.", HttpStatus.SERVICE_UNAVAILABLE),
	TMDB_GATEWAY_TIMEOUT("TMDB_009", "TMDB 서버 응답 시간이 초과되었습니다.", HttpStatus.GATEWAY_TIMEOUT),
	TMDB_MOVIE_NOT_FOUND("TMDB_010", "TMDB내 영화 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_MOVIE_PROVIDER_NOT_FOUND("TMDB_011", "TMDB내 영화의 제공자 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_GENRE_NOT_FOUND("TMDB_012", "TMDB내 장르 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_CERTIFICATION_NOT_FOUND("TMDB_013", "TMDB내 연령등급 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_MOVIELIST_BY_CATEGORY_NOT_FOUND("TMDB_014", "TMDB내 카테고리 타입별 영화목록 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_MOVIELIST_BY_TIMETYPE_NOT_FOUND("TMDB_015", "TMDB내 시간타입별 영화목록 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_PROVIDERS_NOT_FOUND("TMDB_016", "TMDB내 영화 제공자 목록 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_SIMILARLIST_NOT_FOUND("TMDB_017", "TMDB내 비슷한 영화 목록 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_MOVIELIST_SEARCH_NOT_FOUND("TMDB_018", "TMDB내 검색 영화 결과 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_MOVIELIST_FILTER_NOT_FOUND("TMDB_019", "TMDB내 필터 영화 결과 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	TMDB_SESSION_NOT_FOUND("TMDB_020", "TMDB내 세션 결과 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	
	SERVER_ERROR("SERVER_001", "요청을 처리 중 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	MOVIE_NOT_FOUND("MOVIE_001", "영화 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

	GENRE_NOT_FOUND("GENRE_001", "장르 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	
	RATING_DELETE_FAILED("RATING_001", "평가 정보 삭제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	RATING_ADD_FAILED("RATING_002", "평가 정보 저장에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	RATING_NOT_FOUND("RATING_003", "평가 정보가 존재하지 않습니다.", HttpStatus.NO_CONTENT),

	USER_NOT_FOUND("USER_001", "사용자 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	USER_PASSWORD_INVALID("USER_002", "사용자 비밀번호가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
	PASSWORD_SAME_AS_OLD("USER_003", "기존 비밀번호와 동일합니다.", HttpStatus.BAD_REQUEST),
	USER_EMAIL_NOT_FOUND("USER_004", "해당 이메일을 가진 사용자가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
	USER_EMAIL_DUPLICATE("USER_005", "해당 이메일을 사용하는 사용자가 이미 존재합니다.", HttpStatus.BAD_REQUEST),

	SESSION_NOT_FOUND("SESSION_001", "세션 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

	DATABASE_ERROR("DB_001", "DB 서버 오류가 발생했습니다..", HttpStatus.INTERNAL_SERVER_ERROR),
	DATABASE_DUPLICATE_KEY("DB_002", "DB PK/Unique 제약 조건 위반되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	DATABASE_DATA_INTERGRITY_VIOLATION("DB_003", "DB FK 제약 조건, NOT NULL 위반 등 데이터 무결성 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),  

	;

	private final String code;
	private final String msg;
	private final HttpStatus status;
}
