package com.netpickz.core.external.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbCertificationResponse {
	
	@JsonProperty("certification")
	private String certification;
	@JsonProperty("meaning")
	private String meaning;
	@JsonProperty("order")
	private Integer order;
}
