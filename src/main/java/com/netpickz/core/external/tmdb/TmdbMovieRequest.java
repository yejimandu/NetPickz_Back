package com.netpickz.core.external.tmdb;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class TmdbMovieRequest extends TmdbListRequest{
	
	private Integer movieId;
	private Integer rating;
	private String sessionId;
	private String guestSessionId;
}
