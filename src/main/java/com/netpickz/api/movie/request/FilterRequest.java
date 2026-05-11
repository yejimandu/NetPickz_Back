package com.netpickz.api.movie.request;

import com.netpickz.common.enumType.SortType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FilterRequest {
	private int page;
	private String withGenres;
	private String withPeople;
	private SortType sortType;
	private Boolean includeAdult; // 성인용 콘텐츠 포함 여부
	//
	private String certification; // 단일 등급 필터
	private String certificationGte; // 범위 필터 ( ~이상) 
	private String certificationLte; // 범위 필터 ( ~이하)
	private String certificationCountry; // 등급필터 나라
	
}
