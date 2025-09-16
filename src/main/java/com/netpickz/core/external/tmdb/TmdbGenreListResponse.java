package com.netpickz.core.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbGenreListResponse {
	@JsonProperty("genres")
	private List<TmdbGenreResponse> genres;
}
