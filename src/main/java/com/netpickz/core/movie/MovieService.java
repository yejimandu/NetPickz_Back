package com.netpickz.core.movie;

import java.util.List;
import java.util.Optional;

import com.netpickz.api.request.RatingRequest;
import com.netpickz.common.dto.CertificationDTO;
import com.netpickz.common.dto.GenreDTO;
import com.netpickz.common.dto.ProviderDTO;
import com.netpickz.common.enumType.AsyncType;
import com.netpickz.common.enumType.MovieCategory;

public interface MovieService {

	Optional<MovieDTO> getMovieInfoByExternalId(String id);

	Optional<MovieDTO> getMovieInfoByMovieIdAndType(String movieId, AsyncType type);

	Optional<List<GenreDTO>> getMovieGenres(AsyncType type);

	Optional<List<CertificationDTO>> getMovieCertifications(AsyncType type);

	Optional<List<MovieDTO>> getMovieListByType(MovieCategory category);

	Optional<List<ProviderDTO>> getProviders(AsyncType type);

	Optional<List<MovieDTO>> getProviderByMovieId(String movieId);

	Optional<List<MovieDTO>> getMovieSimilarListByMovieId(String movieId);

	void addRatingByUserId(RatingRequest ratingRequest);

}
