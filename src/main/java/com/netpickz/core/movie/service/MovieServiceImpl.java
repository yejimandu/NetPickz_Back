package com.netpickz.core.movie.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.netpickz.api.movie.request.FilterRequest;
import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.common.dto.CertificationDTO;
import com.netpickz.common.dto.GenreDTO;
import com.netpickz.common.dto.ProviderDTO;
import com.netpickz.common.entity.GenreEntity;
import com.netpickz.common.enumType.AsyncType;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.enumType.MovieCategory;
import com.netpickz.common.enumType.TimeType;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.common.repository.CertificationRepository;
import com.netpickz.common.repository.GenreRepository;
import com.netpickz.common.repository.ProvidersRepository;
import com.netpickz.common.util.IdGenerator;
import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbGenreResponse;
import com.netpickz.core.external.tmdb.TmdbMovieCategory;
import com.netpickz.core.external.tmdb.TmdbMovieRequest;
import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.movie.entity.MovieGenreEntity;
import com.netpickz.core.movie.entity.MovieProviderEntity;
import com.netpickz.core.movie.entity.pk.MovieGenrePK;
import com.netpickz.core.movie.mapper.MovieMapper;
import com.netpickz.core.movie.repository.MovieProviderRepository;
import com.netpickz.core.movie.repository.MovieRepository;
import com.netpickz.core.movie.repository.MovieRepositoryCustom;
import com.netpickz.core.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

	private final MovieRepository movieRepository;
	private final MovieRepositoryCustom movieRepositoryCustom;
	private final GenreRepository genreRepository;
	private final CertificationRepository certificationRepository;
	private final ProvidersRepository providersRepository;
	private final MovieProviderRepository movieProviderRepository;
	private final TmdbClient tmdbClient;
	private final UserService userService;
	private final MovieMapper movieMapper;

	@Transactional
	@Override
	public Optional<MovieDTO> getMovieInfoByExternalId(String id) {
		log.debug("Find MovieInfo By ExternalId. id={}", id);
		var movieDto = movieRepositoryCustom.findByExternalId(id);
		if(movieDto.isPresent()) {
			return movieDto;
		}
		
		var movieId = IdGenerator.getId("MV_");
		var result = fetchMovieInfoOrThrow(movieId, id);
		return  Optional.of(result);
	}

	private MovieDTO fetchMovieInfoOrThrow(String movieId, String id) {
		
		// 0. tmdb api 영화 정보 조회
		var tmdbResponse = tmdbClient.getMovieInfo(TmdbMovieRequest.builder().movieId(Integer.valueOf(id)).build());
		if (tmdbResponse == null) {
			throw new NetPickzException(ErrorCode.TMDB_MOVIE_NOT_FOUND);
		}
		var response = tmdbResponse.getBody();
		
		// 1. 부모 엔티티 저장
		var movieEntity = movieMapper.tmdbMovieToEntity(response, movieId);
		// 2. 자식 엔티티 저장
		var infoEntity = movieMapper.tmdbMovieToInfoEntity(response);
		// 연관관계 양쪽 세팅 (중요!)
		infoEntity.setMovieEntity(movieEntity);
		movieEntity.setMovieInfo(infoEntity);
		
		// 3. genres 엔티티 저장
		var movieGenresEntity = movieMapper.tmdbGenresToMovieGenreEntity(response.getGenres(),movieEntity);
		for (MovieGenreEntity e : movieGenresEntity) {
		    e.setMovieEntity(movieEntity);
		    e.setGenreEntity(genreRepository.findById(e.getId().getGenreId())
		    		.orElseThrow(() -> new NetPickzException(ErrorCode.GENRE_NOT_FOUND)));
		}
		movieEntity.setGenres(movieGenresEntity);
		
		// 4. provider 엔티티 저장(개봉 중인 영화는 안뜨니까 존재하는 애들만)
		var tmdbProviderByIdResponse = tmdbClient.getWatchProviderList(TmdbMovieRequest.builder().movieId(Integer.valueOf(id)).build()).getBody();
			var providerResponse = tmdbProviderByIdResponse.getResults().get("KR");
			if(providerResponse != null) {
				var movieProvidersEntity = movieMapper.tmdbWatchProvidersToEntity(providerResponse.getFlatrate(), movieEntity);
				for(MovieProviderEntity e : movieProvidersEntity) {
					e.setMovieEntity(movieEntity);
					e.setProvidersEntity(providersRepository.findById(e.getId().getProviderId()).orElse(null));
				}
				movieEntity.setProviders(movieProvidersEntity);
			}
		
		var savedMovie = movieRepository.save(movieEntity);
		log.debug("Movie saved: movieId={}", savedMovie.getMovieId());
		var genrsIds = response.getGenres().stream().map(e -> e.getId()).toList();
		var movieDTO = movieMapper.entityToDto(savedMovie);
		movieDTO.setGenres(genrsIds);
		return movieDTO;
	}

	@Override
	public Optional<MovieDTO> getMovieInfoByMovieIdAndType(String movieId, AsyncType type) {
		log.debug("Find MovieInfo. movieId={}, asynType={}", movieId, type);
		
		if(Boolean.valueOf(type.getValue())) {
			var movie = movieRepository.findById(movieId);
			var existGenres = movie.get().getGenres().stream().map((e) -> e.getId().getGenreId()).toList();
			
			var tmdbResponse = tmdbClient.getMovieInfo(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.get().getId())).build());
			if (tmdbResponse == null) {
				throw new NetPickzException(ErrorCode.TMDB_MOVIE_NOT_FOUND);
			}
			var response = tmdbResponse.getBody();
			var movieInfo = movie.get().getMovieInfo();
			movieInfo.setOverView(response.getOverview());
			movieInfo.setPosterPath(response.getPosterPath());
			movieInfo.setRuntime(response.getRuntime());
			movieInfo.setReleaseDate(response.getReleaseDate());
			movieInfo.setStatus(response.getStatus());

			response.getGenres().stream().forEach((e) -> {
				if(!genreRepository.existsById(String.valueOf(e.getId()))) {
					genreRepository.save(GenreEntity.builder().id(String.valueOf(e.getId())).name(e.getName()).build());
				}
			});
			
			var tmdbGenres = response.getGenres().stream().map((e) -> e.getId()).collect(Collectors.toList());
			tmdbGenres.stream().filter(id -> !existGenres.contains(String.valueOf(id)))
			.forEach(id -> {
				movie.get().getGenres().add(
						MovieGenreEntity.builder().id(MovieGenrePK.builder().genreId(String.valueOf(id)).movieId(movieId).build())
						.movieEntity(movie.get())
						.genreEntity(GenreEntity.builder().id(String.valueOf(id))
								.name(response.getGenres().stream().filter((g) -> g.getId() == id).findFirst().map(TmdbGenreResponse::getName).orElse(null))
								.build())
						.build()
				);
			});
			movie.get().setMovieInfo(movieInfo);
			var upsertMovie = movieRepository.save(movie.get());
			log.debug("Movie upsert: movieId={}", upsertMovie.getMovieId());
		}
		return  movieRepositoryCustom.findByMovieId(movieId);
	}

	@Override
	public Optional<List<GenreDTO>> getMovieGenres(AsyncType type) {
		log.debug("Find MovieGenres. asynType={}", type);
		if(Boolean.valueOf(type.getValue())) {
			var tmdbGenreResponse = tmdbClient.getGenreList().getBody();
			if(tmdbGenreResponse == null) {
				throw new NetPickzException(ErrorCode.TMDB_GENRE_NOT_FOUND);
			}
			var genreEntity = movieMapper.tmdbGenresToEntity(tmdbGenreResponse.getGenres());
			genreRepository.saveAll(genreEntity);
		}
		var genresEntity = genreRepository.findAll();
		var genres = movieMapper.genreToGenreDTO(genresEntity);
		log.info("movieGenres Found.count={}",genres.size());
		return Optional.of(genres);
	}

	@Override
	public Optional<List<CertificationDTO>> getMovieCertifications(AsyncType type) {
		log.debug("Find Certifications. asynType={}", type);
		if(Boolean.valueOf(type.getValue())) {
			var tmdbCetificationResponse =  tmdbClient.getCertificationList().getBody();
			if(tmdbCetificationResponse == null ) {
				throw new NetPickzException(ErrorCode.TMDB_CERTIFICATION_NOT_FOUND);
			}
			
			var certifications = tmdbCetificationResponse.getCertifications().get("KR").stream()
					.map(e -> {
						if("Restricted Screening".equals(e.getCertification())) {
							e.setCertification("RS");
						}
						return e;
					}).toList();
			
			var certificationEntitys = movieMapper.tmdbCertificationsToEntity(certifications);
			
			certificationRepository.saveAll(certificationEntitys);
		}
		var certifications = certificationRepository.findAll();
		var certificationDtos = movieMapper.entityToDTO(certifications);
		log.info("movie Certifications. count={}", certificationDtos.size());
		return Optional.of(certificationDtos);
	}


	@Override
	public Optional<List<MovieDTO>> getMovieListByType(MovieCategory category) {
		log.debug("Find CategoryMovieList. category={}", category);
		var tmdbMoviesResponse = tmdbClient.getMovieList(TmdbMovieCategory.valueOf(category.getValue())).getBody();
		if(tmdbMoviesResponse == null) {
			throw new NetPickzException(ErrorCode.TMDB_MOVIELIST_BY_CATEGORY_NOT_FOUND);
		}
		
		var typeMovieDtos = movieMapper.tmdbMoviesToMovieDto(tmdbMoviesResponse.getResults());
		log.info("MovieList By Category Found. count={}", typeMovieDtos.size());
		return Optional.of(typeMovieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListByTimeType(TimeType timeType) {
		log.debug("Find ovieListByTimeType. TimeType={}", timeType);
		var tmdbMoviesResponse = tmdbClient.getMovieTrendList(timeType.getValue()).getBody();
		if(tmdbMoviesResponse == null) {
			throw new NetPickzException(ErrorCode.TMDB_MOVIELIST_BY_TIMETYPE_NOT_FOUND);
		}
		var timeTypeMovieDtos = movieMapper.tmdbMoviesToMovieDto(tmdbMoviesResponse.getResults());
		log.info("MovieList By TimeType Found. count={}", timeTypeMovieDtos.size());
		return Optional.of(timeTypeMovieDtos);
	}

	@Override
	public Optional<List<ProviderDTO>> getProviders(AsyncType type) {
		log.debug("Find Providers. AsyncType={}", type);
		if(Boolean.valueOf(type.getValue())) {
			var tmdbProviderResponse = tmdbClient.getProviderList().getBody();
			if(tmdbProviderResponse == null) {
				throw new NetPickzException(ErrorCode.TMDB_PROVIDERS_NOT_FOUND);
			}
			var providerEntitys = movieMapper.tmdbProvidersToEntity(tmdbProviderResponse.getResults());
			providersRepository.saveAll(providerEntitys);
		}
		var providerEntitys = providersRepository.findAll();
		var providerDtos = movieMapper.providerToDTO(providerEntitys);
		log.info("ProviderList Found. count={}", providerDtos.size());
		return Optional.of(providerDtos);
	}

	@Override
	public List<MovieDTO> getProviderByMovieId(String movieId) {
		log.debug("Find ProvidersByMovieId. movieId={}", movieId);
		var movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND)); 
		
		var tmdbProviderByIdResponse = tmdbClient.getWatchProviderList(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId())).build()).getBody();
		var providerResponse = tmdbProviderByIdResponse.getResults().get("KR");
		if(providerResponse != null) {
			var movieProvidersEntity = movieMapper.tmdbWatchProvidersToEntity(providerResponse.getFlatrate(), movie);
			for(MovieProviderEntity e : movieProvidersEntity) {
				e.setMovieEntity(movie);
				e.setProvidersEntity(providersRepository.findById(e.getId().getProviderId()).orElse(null));
			}
			var savedMovieProvider = movieProviderRepository.saveAll(movieProvidersEntity);
			var movieDtos = movieMapper.entitysToDto(savedMovieProvider);
			log.info("ProviderMovieList Found. movieId={}, count={}", movieId, movieDtos.size());
			return movieDtos;
		}
		return List.of();
	}

	@Override
	public Optional<List<MovieDTO>> getMovieSimilarListByMovieId(String movieId) {
		log.debug("Find MovieSimilarList. movieId={}", movieId);
		// TODO 중복처리 및 기존에 있는 값이면 처리
		var movie = movieRepository.findById(movieId)
					.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
		var tmdbSimilarMoviesResponse = tmdbClient.getSimilarMovieListById(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId())).build()).getBody();
		if(tmdbSimilarMoviesResponse == null ) {
			throw new NetPickzException(ErrorCode.TMDB_SIMILARLIST_NOT_FOUND);
		}
		var similarMovieDtos = movieMapper.tmdbMoviesToMovieDto(tmdbSimilarMoviesResponse.getResults());
		log.info("ProviderMovieList Found. movieId={}, count={}", movieId, similarMovieDtos.size());
		return Optional.of(similarMovieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListBySearch(String title) {
		log.debug("Find MovieList. title={}", title);
		var tmdbMoviesResponse = tmdbClient.getMovieListBySearch(title).getBody();
		if(tmdbMoviesResponse == null ) {
			throw new NetPickzException(ErrorCode.TMDB_MOVIELIST_SEARCH_NOT_FOUND);
		}
		var movieDtos = movieMapper.tmdbMoviesToMovieDto(tmdbMoviesResponse.getResults());
		log.info("MovieList Found. title={}, count={}", title, movieDtos.size());
		return Optional.of(movieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListByFilter(FilterRequest filterRequest) {
		log.debug("Find MovieList By Filter. filter={}", filterRequest.toString());
		var tmdbMoviesResponse = tmdbClient.getMovieListByFilter(filterRequest).getBody();
		if(tmdbMoviesResponse == null) {
			throw new NetPickzException(ErrorCode.TMDB_MOVIELIST_FILTER_NOT_FOUND);
		}
		var movieDtos = movieMapper.tmdbMoviesToMovieDto(tmdbMoviesResponse.getResults());
		log.info("MovieList Found. count={}", movieDtos.size());
		return Optional.of(movieDtos);
	}

	@Override
	public Optional<RatingDTO> addRatingByUserId(String movieId, RatingRequest request) {
		log.debug("Add Rating. movieId={}, rating={}, sessionId={}", movieId, request.getValue(), request.getSessionId());
		var movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
		var tmdbRatingResponse = tmdbClient.addRating(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId()))
				.sessionId(request.getSessionId())
				.rating(request.getValue())
				.build());
		if (tmdbRatingResponse == null ) {
	        log.warn("TMDB Add Rating response is null. movieId={}, rating={}, sessionId={}", movieId, request.getValue(), request.getSessionId());
	        throw new NetPickzException(ErrorCode.RATING_ADD_FAILED);
	    }
		var statusCode = tmdbRatingResponse.getBody().getStatusCode();
		if(statusCode == 1 || statusCode == 12) { // TODO 
			return userService.addRatingByUser(movieId, request);
		}
		return Optional.empty(); // 실패 시 명시적으로 반환
	}

	@Override
	public boolean deleteRatingByUserId(String movieId, String sessionId) {
		log.debug("Delte Rating. movieId={}, sessionId={}", movieId, sessionId);
		var movie = movieRepository.findById(movieId)
					.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
		var tmdbRatingResponse = tmdbClient.deleteRating(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId()))
				.sessionId(sessionId)
				.build());
	    if (tmdbRatingResponse == null ) {
	        log.warn("TMDB deleteRating response is null. movieId={}, sessionId={}", movieId, sessionId);
	        throw new NetPickzException(ErrorCode.RATING_DELETE_FAILED);
	    }

		var statusCode = tmdbRatingResponse.getBody().getStatusCode();
		if(statusCode == 13 ) { // TODO code enum 값으로 
			userService.deleteRatingByUser(movieId, sessionId); 
			log.info("Rating deleted successfully. movieId={}, sessionId={}", movieId, sessionId);
			return true;
		}
		throw new NetPickzException(ErrorCode.RATING_DELETE_FAILED);
	}

}
