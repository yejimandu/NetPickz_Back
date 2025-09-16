package com.netpickz.core.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbProviderResponse {

	@JsonProperty("link")
	private String link;
	@JsonProperty("rent")
	private List<TmdbWatchProvider> rent;
	@JsonProperty("flatrate")
	private List<TmdbWatchProvider> flatrate;
	@JsonProperty("buy")
	private List<TmdbWatchProvider> buy;
}
