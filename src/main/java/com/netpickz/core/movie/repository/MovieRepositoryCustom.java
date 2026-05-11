package com.netpickz.core.movie.repository;

import java.util.Optional;

import com.netpickz.core.movie.dto.MovieDTO;

public interface MovieRepositoryCustom {

	Optional<MovieDTO> findByMovieId(String movieId);
	Optional<MovieDTO> findByExternalId(String id);
	
}
