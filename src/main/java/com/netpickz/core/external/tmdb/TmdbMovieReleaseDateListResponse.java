package com.netpickz.core.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieReleaseDateListResponse {
	
	@JsonProperty("results") // TODO results 정리
	private  List<TmdbMovieReleaseDateResponse> results;
}
