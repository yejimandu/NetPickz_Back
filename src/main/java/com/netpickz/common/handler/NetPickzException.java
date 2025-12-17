package com.netpickz.common.handler;

import com.netpickz.common.enumType.ErrorCode;

public class NetPickzException  extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private ErrorCode errorCode;

	public NetPickzException (ErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
