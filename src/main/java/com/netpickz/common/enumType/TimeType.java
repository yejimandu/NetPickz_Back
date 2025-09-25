package com.netpickz.common.enumType;

public enum TimeType {
	일간("day"),
	주간("week");
	
	private final String value;
	TimeType(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
}
