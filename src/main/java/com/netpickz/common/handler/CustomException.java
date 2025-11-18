package com.netpickz.common.handler;

import com.netpickz.common.enumType.ErrorCode;

public class CustomException extends RuntimeException{

	private ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
