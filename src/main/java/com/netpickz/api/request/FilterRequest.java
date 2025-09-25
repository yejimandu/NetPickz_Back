package com.netpickz.api.request;

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
	private int pageNum;
	private String withGenres;
	private String withPeople;
	private SortType sortType;
	private Boolean includeAdult;
	
}
