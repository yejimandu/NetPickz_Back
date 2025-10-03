package com.netpickz.core.external.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbRatingResponse {
	
	@JsonProperty("success")
	private Boolean success;
	@JsonProperty("status_code")
	private Integer statusCode;
	@JsonProperty("status_message")
	private String statusMessage;
}