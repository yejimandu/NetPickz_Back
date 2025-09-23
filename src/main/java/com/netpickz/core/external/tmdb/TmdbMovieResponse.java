package com.netpickz.core.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
public class TmdbMovieResponse {

	private boolean adult;
	
	@JsonProperty("poster_path")
	private String posterPath;
	
	@JsonProperty("genre_ids")
	private List<Integer> genres;
	
	@JsonProperty("id")
	private Integer id;
	
//	@JsonProperty("origin_country")
//	private String originCountry;
	
	@JsonProperty("original_language")
	private String originalLanguage;
	
	@JsonProperty("overview")
	private String overview;
	
	@JsonProperty("release_date")
	private String releaseDate;
	
	@JsonProperty("runtime")
	private Integer runtime;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("title")
	private String title;
	
	// 추가 및 삭제 response
	private  boolean success;
	private  Integer status_code;
	private  String status_message;
	
	// 평점
	private Integer rating;
	
}
