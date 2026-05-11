package com.netpickz.common.handler;

import org.springframework.security.core.AuthenticationException;

import com.netpickz.common.enumType.ErrorCode;

//public class NetPickzException  extends RuntimeException {
public class NetPickzException  extends  AuthenticationException {

	private static final long serialVersionUID = 1L;
	private ErrorCode errorCode;

	public NetPickzException (ErrorCode errorCode) {
//		super();
		super(errorCode.getMsg());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
