package com.netpickz.core.movie.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.netpickz.common.dto.CertificationDTO;
import com.netpickz.common.dto.GenreDTO;
import com.netpickz.common.dto.ProviderDTO;
import com.netpickz.common.entity.CertificationEntity;
import com.netpickz.common.entity.GenreEntity;
import com.netpickz.common.entity.ProvidersEntity;
import com.netpickz.core.external.tmdb.TmdbCertificationResponse;
import com.netpickz.core.external.tmdb.TmdbGenreResponse;
import com.netpickz.core.external.tmdb.TmdbMovieResponse;
import com.netpickz.core.external.tmdb.TmdbWatchProviderResponse;
import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.movie.entity.MovieGenreEntity;
import com.netpickz.core.movie.entity.MovieInfoEntity;
import com.netpickz.core.movie.entity.MovieProviderEntity;
import com.netpickz.core.movie.entity.pk.MovieGenrePK;
import com.netpickz.core.movie.entity.pk.MovieProviderPK;

@Mapper(componentModel = "spring")
public interface MovieMapper {
	
	@Mapping(source = "movieId", target = "movieId")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "providers", ignore = true)
	@Mapping(target = "genres", ignore = true)
	MovieEntity tmdbMovieToEntity(TmdbMovieResponse tmdbMovie, String movieId);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "certificationEntity", ignore = true)
	@Mapping(source = "tmdbMovie.overview", target = "overView")
	MovieInfoEntity tmdbMovieToInfoEntity(TmdbMovieResponse tmdbMovie);
	
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "certificationEntity", ignore = true)
	@Mapping(source = "tmdbMovie.overview", target = "overView")
	MovieInfoEntity tmdbMovieToInfoEntity(TmdbMovieResponse tmdbMovie, String movieId);
	
	@Mapping(target = "genres", ignore = true)
	@Mapping(source = "movieInfo.posterPath", target = "posterPath")
	@Mapping(source = "movieInfo.releaseDate", target = "releaseDate")
	@Mapping(source = "movieInfo.overView", target = "overview")
	@Mapping(source = "movieInfo.runtime", target = "runtime")
	@Mapping(source = "movieInfo.status", target = "status")
	MovieDTO entityToDto(MovieEntity movieEntity);

	@Mapping(source = "overView", target = "overview")
	@Mapping(source = "movieEntity.id", target = "id")
	@Mapping(source = "movieEntity.title", target = "title")
	MovieDTO entityToDto(MovieInfoEntity movieInfoEntity);
	
//	MovieDTO movieToMovieDTO(MovieInfoEntity movieInfoEntity);
	
//	MovieEntity tmdbMovieToEntity(TmdbMovieResponse tmdbMovie);
//	MovieInfoEntity tmdbMovieToInfoEntity(TmdbMovieResponse tmdbMovie);
	
	@Mapping(source = "genreIds", target = "genres")
    MovieDTO tmdbMovieToMovieDto(TmdbMovieResponse tmdbMovie);
	List<MovieDTO> tmdbMoviesToMovieDto(List<TmdbMovieResponse> tmdbmovies);
	
	@Mapping(source = "providerId", target = "id")
	@Mapping(source = "providerName", target = "name")
	@Mapping(source = "displayPriority", target = "orderNum")
	ProvidersEntity tmdbProviderToEntity(TmdbWatchProviderResponse tmdbMovie);
	List<ProvidersEntity> tmdbProvidersToEntity(List<TmdbWatchProviderResponse> results);
	List<ProviderDTO> providerToDTO(List<ProvidersEntity> providerEntitys);
	
	@Mapping(source = "order", target = "orderNum")
	@Mapping(source = "certification", target = "certificationId")
	CertificationEntity tmdbCertificationToEntity(TmdbCertificationResponse tmdbCertification);
	List<CertificationEntity> tmdbCertificationsToEntity(List<TmdbCertificationResponse> results);
	
	@Mapping(source = "orderNum", target = "order")
	CertificationDTO entityToDTO(CertificationEntity entity);
	List<CertificationDTO> entityToDTO(List<CertificationEntity> entitys);
	
	GenreEntity tmdbGenreToEntity(TmdbGenreResponse tmdbGenre);
	List<GenreEntity> tmdbGenresToEntity(List<TmdbGenreResponse> tmdbGenres);
	
	default MovieGenrePK createMovieGenrePK( MovieEntity movieEntity, Integer genreId) {
	    return new MovieGenrePK(movieEntity.getMovieId(), String.valueOf(genreId));
	}

	@Mapping(target = "id", expression = "java(createMovieGenrePK(movieEntity, tmdbGenre.getId()))")
	@Mapping(target = "createdAt", ignore = true)
	MovieGenreEntity tmdbGenreToMovieGenreEntity(TmdbGenreResponse tmdbGenre, MovieEntity movieEntity);
	
	default List<MovieGenreEntity> tmdbGenresToMovieGenreEntity(List<TmdbGenreResponse> tmdbGenres, MovieEntity movieEntity) {
		 List<MovieGenreEntity> result = new ArrayList<>();
		 for(TmdbGenreResponse genre : tmdbGenres) {
			 result.add(tmdbGenreToMovieGenreEntity(genre, movieEntity));
		 }
	    return result;
	}

	List<GenreDTO> genreToGenreDTO(List<GenreEntity> genreEntitys);

	default MovieProviderPK createMovieProviderPK( MovieEntity movieEntity, String providerId) {
	    return new MovieProviderPK(movieEntity.getMovieId(), providerId);
	}
	
	@Mapping(target = "id", expression = "java(createMovieProviderPK(movieEntity, String.valueOf(tmdb.getProviderId())))")
	@Mapping(target = "createdAt", ignore = true)
	MovieProviderEntity tmdbWatchProviderToEntity(TmdbWatchProviderResponse tmdb, MovieEntity movieEntity);
	
	default List<MovieProviderEntity> tmdbWatchProvidersToEntity(List<TmdbWatchProviderResponse> flatrate, MovieEntity movieEntity){
		List<MovieProviderEntity> result = new ArrayList<>();
		 for(TmdbWatchProviderResponse provider : flatrate) {
			 result.add(tmdbWatchProviderToEntity(provider, movieEntity));
		 }
	    return result;
	}
	
	@Mapping(source = "id.movieId", target = "movieId")
	@Mapping(source = "id.providerId", target = "providerId")
	@Mapping(target = "posterPath", ignore = true)
	@Mapping(target = "releaseDate", ignore = true)
	@Mapping(target = "overview", ignore = true)
	@Mapping(target = "runtime", ignore = true)
	@Mapping(target = "title", ignore = true)
	@Mapping(target = "genres", ignore = true)
	@Mapping(target = "originalLanguage", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "id", ignore = true)
	MovieDTO entityToDto(MovieProviderEntity movieProviderEntity);
	List<MovieDTO> entitysToDto(List<MovieProviderEntity> movieProviderEntities);
}
