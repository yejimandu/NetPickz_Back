package com.netpickz.core.external.tmdb;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class TmdbWatchProviderListResponse {

	@JsonProperty("results")
	private Map<String, TmdbProviderResponse> results;
//	private Map<String, List<TmdbProviderResponse>> results;

}
