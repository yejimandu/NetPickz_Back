package com.netpickz.common.enumType;

public enum MovieCategory {
	인기순("popular"),
	평점순("top_rated"),
	예정작("upcoming"),
	상영작("now_playing");
	
	private final String value;
	MovieCategory(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
}
