package com.netpickz.core.external.tmdb;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false) // 
public class TmdbMovieRequest extends TmdbListRequest{
	
	private Integer movieId;
	private Double rating;
	private String sessionId;
//	private String guestSessionId;
}
