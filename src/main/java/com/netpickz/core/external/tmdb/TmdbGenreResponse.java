package com.netpickz.core.external.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbGenreResponse {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("name")
	private String name;
	
}
