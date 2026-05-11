package com.netpickz.core.external.tmdb;

public enum TmdbMovieCategory {
	
	NOW_PLAYING("/movie/now_playing"),
	POPULAR("/movie/popular"),
	TOP_RATED("/movie/top_rated"),
	UPCOMING("/movie/upcoming");
	
	private final String path;
	TmdbMovieCategory(String path) {
		this.path = path;
	}
	public String getPath() {return path; }
}
