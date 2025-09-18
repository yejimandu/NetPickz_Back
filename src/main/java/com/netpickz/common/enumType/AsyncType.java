package com.netpickz.common.enumType;

public enum AsyncType {
	갱신("Y"),
	미갱신("N");
	
	private final String value;
	AsyncType(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
}
