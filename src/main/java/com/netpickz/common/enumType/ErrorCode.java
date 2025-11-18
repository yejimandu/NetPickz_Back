package com.netpickz.common.enumType;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	ERROR_TEST(HttpStatus.BAD_REQUEST , "ERROR_TEST", "에러 테스트입니다.");
	
	private final HttpStatus status;
	private final String code;
	private final String msg;
}
