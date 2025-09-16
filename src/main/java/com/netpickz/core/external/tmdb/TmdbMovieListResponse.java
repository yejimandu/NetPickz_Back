package com.netpickz.core.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbMovieListResponse {
	
	@JsonProperty("results")
	private List<TmdbMovieResponse> results;
	@JsonProperty("page")
	private Integer page;
	@JsonProperty("total_pages")
	private Integer totalPages;
	@JsonProperty("total_results")
	private Integer totalResults;
	
}
