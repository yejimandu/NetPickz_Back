package com.netpickz.core.movie.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.netpickz.api.movie.request.FilterRequest;
import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.common.dto.CertificationDTO;
import com.netpickz.common.dto.GenreDTO;
import com.netpickz.common.dto.ProviderDTO;
import com.netpickz.common.enumType.AsyncType;
import com.netpickz.common.enumType.MovieCategory;
import com.netpickz.common.enumType.TimeType;
import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.dto.MovieListPageDTO;
import com.netpickz.core.movie.dto.RatingDTO;

public interface MovieService {

	Optional<MovieDTO> getMovieInfoByExternalId(String id);

	Optional<MovieDTO> getMovieInfoByMovieIdAndType(String movieId, AsyncType type);

	CompletableFuture<List<MovieDTO>> getMovieListByTimeType(TimeType timeType);
	
	List<GenreDTO> getMovieGenres(AsyncType type);

	List<CertificationDTO> getMovieCertifications(AsyncType type);

	CompletableFuture<List<MovieDTO>> getMovieListByType(MovieCategory category);
//	List<MovieDTO> getMovieListByType(MovieCategory category);

	List<ProviderDTO> getProviders(AsyncType type);

	List<MovieDTO> getProviderByMovieId(String movieId);

	CompletableFuture<List<MovieDTO>> getMovieSimilarListByMovieId(String movieId);

	CompletableFuture<MovieListPageDTO> getMovieListBySearch(String title, int page);

	CompletableFuture<List<MovieDTO>> getMovieListByFilter(FilterRequest filterRequest);

	Optional<RatingDTO> addRatingByUserId(String movieId, RatingRequest ratingRequest);

	boolean deleteRatingByUserId(String movieId, String sessionId);

	// TODO 추후 
	List<MovieDTO> getMovieCertification(String id);
}
