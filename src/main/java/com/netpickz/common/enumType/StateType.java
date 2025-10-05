package com.netpickz.common.enumType;

public enum StateType {
	정상("ACTIVE"),
	휴면("DORMANT"),
	탈퇴("WITHDRAWN");
	
	private final String value;
	StateType(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
}
