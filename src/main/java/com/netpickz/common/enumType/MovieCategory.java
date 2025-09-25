package com.netpickz.common.enumType;

public enum MovieCategory {
	인기순("POPULAR"),
	평점순("TOP_RATED"),
	예정작("UPCOMING"),
	상영작("NOW_PLAYING");
	
	private final String value;
	MovieCategory(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
}
