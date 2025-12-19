package com.netpickz.core.movie.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.netpickz.api.movie.request.FilterRequest;
import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.common.dto.CertificationDTO;
import com.netpickz.common.dto.GenreDTO;
import com.netpickz.common.dto.ProviderDTO;
import com.netpickz.common.entity.CertificationEntity;
import com.netpickz.common.entity.GenreEntity;
import com.netpickz.common.entity.ProvidersEntity;
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
import com.netpickz.core.external.tmdb.TmdbMovieCategory;
import com.netpickz.core.external.tmdb.TmdbMovieRequest;
import com.netpickz.core.external.tmdb.TmdbProviderResponse;
import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.movie.entity.MovieGenreEntity;
import com.netpickz.core.movie.entity.MovieInfoEntity;
import com.netpickz.core.movie.entity.MovieProviderEntity;
import com.netpickz.core.movie.entity.pk.MovieGenrePK;
import com.netpickz.core.movie.entity.pk.MovieProviderPK;
import com.netpickz.core.movie.repository.MovieGenreRepository;
import com.netpickz.core.movie.repository.MovieInfoRepository;
import com.netpickz.core.movie.repository.MovieProviderRepository;
import com.netpickz.core.movie.repository.MovieRepository;
import com.netpickz.core.movie.repository.MovieRepositoryCustom;
import com.netpickz.core.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

	private final MovieRepository movieRepository;
	private final MovieRepositoryCustom movieRepositoryCustom;
	private final MovieInfoRepository movieInfoRepository;
	private final GenreRepository genreRepository;
	private final CertificationRepository certificationRepository;
	private final ProvidersRepository providersRepository;
	private final MovieProviderRepository movieProviderRepository;
	private final MovieGenreRepository movieGenreRepository;
	private final TmdbClient tmdbClient;
	private final UserService userService;
	
	@Override
	public Optional<MovieDTO> getMovieInfoByExternalId(String id) {
		log.debug("Find MovieInfo By ExternalId. id={}", id);
		var movieDto = movieRepositoryCustom.findByExternalId(id);
		if(movieDto.isPresent()) {
			return movieDto;
		}
		
		var movieID = IdGenerator.getId("MV_");
		var tmdbMovieResponse = tmdbClient.getMovieInfo(TmdbMovieRequest.builder().movieId(Integer.valueOf(id)).build()).getBody();
		// TODO tmdb 없는것도 처리 필요
		// 1. 부모 엔티티 저장
		var movieEntity = MovieEntity.builder().id(String.valueOf(tmdbMovieResponse.getId())).movieId(movieID).title(tmdbMovieResponse.getTitle()).build();
		var movie = movieRepository.saveAndFlush(movieEntity);
		
		// 2. 자식 엔티티 저장
		var movieInfoEntity = MovieInfoEntity.builder().movieEntity(movie)
				.overView(tmdbMovieResponse.getOverview())
				.posterPath(tmdbMovieResponse.getPosterPath())
				.releaseDate(tmdbMovieResponse.getReleaseDate()).build();
		movieInfoRepository.saveAndFlush(movieInfoEntity);
		
		// 3. genres 엔티티 저장
		var movieGenresEntity = tmdbMovieResponse.getGenres().stream().map((e) -> MovieGenreEntity.builder()
				.id(MovieGenrePK.builder().genreId(String.valueOf(e.getId())).movieId(movieID).build())
				.movieEntity(movieEntity)
				.genreEntity(GenreEntity.builder().id(String.valueOf(e.getId())).build())
				.build())
				.collect(Collectors.toList());
		
		var genres = movieGenreRepository.saveAll(movieGenresEntity);
		var genrsIds = genres.stream().map(e -> Integer.valueOf(e.getGenreEntity().getId())).toList();

		var movieDTO = MovieDTO.builder()
				.movieId(movieID)
				.posterPath(movieInfoEntity.getPosterPath())
				.releaseDate(movieInfoEntity.getReleaseDate())
				.overview(movieInfoEntity.getOverView())
				.genres(genrsIds)
//				.runtime(tmdbMovieResponse.getRuntime())
				.title(movie.getTitle())
				.id(movie.getId()).build();
		
		return  Optional.of(movieDTO);
	}

	@Override
	public Optional<MovieDTO> getMovieInfoByMovieIdAndType(String movieId, AsyncType type) {
		log.debug("Find MovieInfo. movieId={}, asynType={}", movieId, type);
		// TODO type true 인경우.
		if(Boolean.valueOf(type.getValue())) {
			var tmdbMovie = movieRepository.findById(movieId).get();
			var tmdbMovieResponse = tmdbClient.getMovieInfo(TmdbMovieRequest.builder().movieId(Integer.valueOf(tmdbMovie.getId())).build()).getBody();
			var movieEntity = MovieEntity.builder().id(String.valueOf(tmdbMovieResponse.getId())).title(tmdbMovieResponse.getTitle()).build();
			movieRepository.saveAndFlush(movieEntity);
//			var movieInfoEntity = MovieInfoEntity.builder().movieId(movieId).overView(tmdbMovieResponse.getOverview()).posterPath(tmdbMovieResponse.getPosterPath())
//					.releaseDate(tmdbMovieResponse.getReleaseDate()).build();
//			movieInfoRepository.save(movieInfoEntity);
			// 3. genres 엔티티 저장
			
		}
		return  movieRepositoryCustom.findByMovieId(movieId);
	}

	@Override
	public Optional<List<GenreDTO>> getMovieGenres(AsyncType type) {
		log.debug("Find MovieGenres. asynType={}", type);
		if(Boolean.valueOf(type.getValue())) {
			var tmdbGenreResponse = tmdbClient.getGenreList().getBody();
			var genreEntity = tmdbGenreResponse.getGenres()
					.stream()
					.map(re -> GenreEntity.builder().id(String.valueOf(re.getId())).name(re.getName()).build()).toList();
			genreRepository.saveAll(genreEntity);
		}
		var genres = genreRepository.findAll().stream().map(e -> new GenreDTO(e.getId() , e.getName())).toList();
		log.info("movieGenres Found.count={}",genres.size());
		return Optional.of(genres);
	}

	@Override
	public Optional<List<CertificationDTO>> getMovieCertifications(AsyncType type) {
		log.debug("Find Certifications. asynType={}", type);
		if(Boolean.valueOf(type.getValue())) {
			var tmdbCetificationResponse =  tmdbClient.getCertificationList().getBody();
			var certificationEntitys = tmdbCetificationResponse.getCertifications().get("KR").stream().map(re -> CertificationEntity.builder().certification(re.getCertification()).meaning(re.getMeaning()).orderNum(re.getOrder()).build()).toList(); 
			certificationRepository.saveAll(certificationEntitys);
		}
		var certifications = certificationRepository.findAll();
		var certificationDtos = certifications.stream().map(e -> new CertificationDTO(e.getCertification(), e.getMeaning(), e.getOrderNum())).toList();
		log.info("movie Certifications. count={}", certificationDtos.size());
		return Optional.of(certificationDtos);
	}


	@Override
	public Optional<List<MovieDTO>> getMovieListByType(MovieCategory category) {
		log.debug("Find CategoryMovieList. category={}", category);
		var tmdbMoviesResponse = tmdbClient.getMovieList(TmdbMovieCategory.valueOf(category.getValue())).getBody();
		var typeMovieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
															.posterPath(e.getPosterPath())
															.releaseDate(e.getReleaseDate())
															.genres(e.getGenreIds())
															.originalLanguage(e.getOriginalLanguage())
															.title(e.getTitle()).id(String.valueOf(e.getId()))
															.build())
															.toList();
		log.info("MovieList By Category Found. count={}", typeMovieDtos.size());
		return Optional.of(typeMovieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListByTimeType(TimeType timeType) {
		log.debug("Find ovieListByTimeType. TimeType={}", timeType);
		var tmdbMoviesResponse = tmdbClient.getMovieTrendList(timeType.getValue()).getBody();
		var timeTypeMovieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
				.posterPath(e.getPosterPath()).overview(e.getOverview())
				.releaseDate(e.getReleaseDate()).genres(e.getGenreIds())
				.originalLanguage(e.getOriginalLanguage())
				.title(e.getTitle()).id(String.valueOf(e.getId())).build())
				.toList();
		log.info("MovieList By TimeType Found. count={}", timeTypeMovieDtos.size());
		return Optional.of(timeTypeMovieDtos);
	}

	@Override
	public Optional<List<ProviderDTO>> getProviders(AsyncType type) {
		log.debug("Find Providers. AsyncType={}", type);
		if(Boolean.valueOf(type.getValue())) {
			var tmdbProviderResponse =  tmdbClient.getProviderList().getBody();
			var providerEntitys = tmdbProviderResponse.getResults().stream().map(re -> ProvidersEntity.builder()
					.id(String.valueOf(re.getProviderId())).name(re.getProviderName())
					.logoPath(re.getLogoPath()).orderNum(String.valueOf(re.getDisplayPriority()))
					.build()).toList();
			providersRepository.saveAll(providerEntitys);
		}
		var providerDtos = providersRepository.findAll().stream().map(e -> new ProviderDTO(String.valueOf(e.getId()), e.getName(),e.getLogoPath(), e.getOrderNum())).toList();
		log.info("ProviderList Found. count={}", providerDtos.size());
		return Optional.of(providerDtos);
	}


	@Override
	public Optional<List<MovieDTO>> getProviderByMovieId(String movieId) {
		log.debug("Find ProvidersByMovieId. movieId={}", movieId);
		var movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND)); 
		var tmdbProviderByMovieIdResponse = tmdbClient.getWatchProviderList(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId())).build()).getBody();
		var movieProviderEntity = Optional.ofNullable(tmdbProviderByMovieIdResponse.getResults().get("KR"))
				.map(TmdbProviderResponse::getFlatrate)
				.orElse(Collections.emptyList())
				.stream().map(re -> MovieProviderEntity.builder()
						.providersEntity(ProvidersEntity.builder().id(String.valueOf(re.getProviderId())).build())
						.id(MovieProviderPK.builder().movieId(movieId).providerId(String.valueOf(re.getProviderId())).build())
						.movieEntity(movie).build()).toList();
		
		var movieProviders =  movieProviderRepository.saveAll(movieProviderEntity); // DATABASE_ERROR
		var movieDtos = movieProviders.stream().map(e -> new MovieDTO().builder().movieId(e.getMovieEntity().getId())
				.providerId(e.getProvidersEntity().getId()).build()).toList();
		log.info("ProviderMovieList Found. movieId={}, count={}", movieId, movieDtos.size());
		return Optional.of(movieDtos);
	}


	@Override
	public Optional<List<MovieDTO>> getMovieSimilarListByMovieId(String movieId) {
		log.debug("Find MovieSimilarList. movieId={}", movieId);
		// TODO 중복처리 및 기존에 있는 값이면 처리
		var movie = movieRepository.findById(movieId)
					.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
		var tmdbSimilarMoviesResponse = tmdbClient.getSimilarMovieListById(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId())).build()).getBody();
		var similarMovieDtos = tmdbSimilarMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
				.posterPath(e.getPosterPath())
				.overview(e.getOverview())
				.genres(e.getGenreIds())
				.releaseDate(e.getReleaseDate())
				.title(e.getTitle())
				.id(String.valueOf(e.getId())).build()).toList();
		log.info("ProviderMovieList Found. movieId={}, count={}", movieId, similarMovieDtos.size());
		return Optional.of(similarMovieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListBySearch(String title) {
		log.debug("Find MovieList. title={}", title);
		var tmdbMoviesResponse = tmdbClient.getMovieListBySearch(title).getBody();
		var movieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
				.posterPath(e.getPosterPath()).overview(e.getOverview())
				.releaseDate(e.getReleaseDate()).genres(e.getGenreIds())
				.originalLanguage(e.getOriginalLanguage())
				.title(e.getTitle()).id(String.valueOf(e.getId())).build())
				.toList();
		log.info("MovieList Found. title={}, count={}", title, movieDtos.size());
		return Optional.of(movieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListByFilter(FilterRequest filterRequest) {
		log.debug("Find MovieList By Filter. filter={}", filterRequest.toString());
		var tmdbMoviesResponse = tmdbClient.getMovieListByFilter(filterRequest).getBody();
		var movieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
				.posterPath(e.getPosterPath()).overview(e.getOverview())
				.releaseDate(e.getReleaseDate()).genres(e.getGenreIds())
				.originalLanguage(e.getOriginalLanguage())
				.title(e.getTitle()).id(String.valueOf(e.getId())).build())
				.toList();
		log.info("MovieList Found. count={}", movieDtos.size());
		return Optional.of(movieDtos);
	}

	@Override
	public Optional<RatingDTO> addRatingByUserId(String movieId, RatingRequest request) {
		log.debug("Add Rating. movieId={}, rating={}, sessionId={}", movieId, request.getValue(), request.getSessionId());
		//TODO ispresent 아닌것도 처리
		var movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
		var tmdbRatingResponse = tmdbClient.addRating(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId()))
				.sessionId(request.getSessionId())
				.rating(request.getValue())
				.build());
		var statusCode = tmdbRatingResponse.getBody().getStatusCode();
	
		Optional<RatingDTO> rationDTO = Optional.empty();
		if(1 == statusCode || 12 == statusCode) {
			rationDTO = userService.addRatingByUser(movieId, request);
		}
		return rationDTO;
	}

	@Override
	public void deleteRatingByUserId(String movieId, String sessionId) {
		log.debug("Delte Rating. movieId={}, sessionId={}", movieId, sessionId);
		//TODO ispresent 아닌것도 처리
		var movie = movieRepository.findById(movieId)
					.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
		var tmdbRatingResponse = tmdbClient.deleteRating(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId()))
				.sessionId(sessionId)
				.build());
		// TODO DB 에서 삭제
		var statusCode = tmdbRatingResponse.getBody().getStatusCode();
		if(statusCode == 13 ) userService.deleteRatingByUser(movieId, sessionId);
	}

}
