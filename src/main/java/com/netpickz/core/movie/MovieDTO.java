package com.netpickz.core.movie;

import java.util.List;

import com.netpickz.core.external.tmdb.TmdbGenreResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MovieDTO {
	
	private String posterPath;
	private Integer id;
	private String releaseDate;
	private String overview;
	private Integer runtime;
	private String status;
	private String title;
	private String originalLanguage;
	private List<TmdbGenreResponse> genres;
}
