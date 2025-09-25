package com.netpickz.common.enumType;

public enum SortType {
	제목_오름차순("title.asc"),
	제목_내림차순("title.desc"),
	핫한_오름차순("popularity.asc"),
	핫한_내림차순("popularity.desc"),
	개봉일_내림차순("primary_release_date.desc"),
	개봉일_오름차순("primary_release_date.asc"),
	평점_내림차순("vote_average.desc"),
	평점_오름차순("vote_average.asc");
	
	private final String value;
	SortType(String value) {
		this.value = value;
	}
	public String getValue() {
   		return value;                     
	}                        
}