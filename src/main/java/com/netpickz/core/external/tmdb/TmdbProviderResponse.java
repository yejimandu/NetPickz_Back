package com.netpickz.core.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbProviderResponse {

	@JsonProperty("link")
	private String link;
	@JsonProperty("rent")
	private List<TmdbWatchProviderResponse> rent;
	@JsonProperty("flatrate")
	private List<TmdbWatchProviderResponse> flatrate;
	@JsonProperty("buy")
	private List<TmdbWatchProviderResponse> buy;
}
