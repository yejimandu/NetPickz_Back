package com.netpickz.core.movie.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.api.mail.MailController;
import com.netpickz.api.movie.request.FilterRequest;
import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.reco.RecommendController;
import com.netpickz.common.dto.CertificationDTO;
import com.netpickz.common.dto.GenreDTO;
import com.netpickz.common.dto.ProviderDTO;
import com.netpickz.common.entity.CertificationEntity;
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
import com.netpickz.core.external.tmdb.TmdbMovieListResponse;
import com.netpickz.core.external.tmdb.TmdbMovieRequest;
import com.netpickz.core.external.tmdb.TmdbMovieResponse;
import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.dto.MovieListPageDTO;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.movie.entity.MovieGenreEntity;
import com.netpickz.core.movie.entity.MovieProviderEntity;
import com.netpickz.core.movie.entity.pk.MovieGenrePK;
import com.netpickz.core.movie.mapper.MovieMapper;
import com.netpickz.core.movie.repository.MovieInfoRepository;
import com.netpickz.core.movie.repository.MovieProviderRepository;
import com.netpickz.core.movie.repository.MovieRepository;
import com.netpickz.core.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {


    private final MovieInfoRepository movieInfoRepository;
	private final MovieRepository movieRepository;
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
		var movieDto = movieRepository.findByExternalId(id);
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
		// 5. certifications 값 가져와서 저장
		var releaseRes = tmdbClient.getReleaseDateList(TmdbMovieRequest.builder().movieId(response.getId()).build()).getBody();
		if(releaseRes != null) {
	        var krReleaseDate = releaseRes.getResults().stream()
	                .filter(r -> "KR".equals(r.getCountryCode()))
	                .flatMap(r -> r.getReleaseDates().stream())
	                .filter(rd -> rd.getType() == 3 || rd.getType() == 4)
	                .findFirst();
	        if(krReleaseDate.isPresent()) {
	        	var certCode = krReleaseDate.get().getCertification();
	        	if ("18".equals(certCode)) {
	        	    certCode = "19"; // 한국 기준 최신 등급으로 보정
	        	}
	        	infoEntity.setCertificationEntity(CertificationEntity.builder().certificationId(certCode).build());
	        }
		}
		// 연관관계 양쪽 세팅 (중요!)
		infoEntity.setMovieEntity(movieEntity);
		movieEntity.setMovieInfo(infoEntity);
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
		return  movieRepository.findByMovieId(movieId);
	}

	@Override
	public List<GenreDTO> getMovieGenres(AsyncType type) {
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
		return genres;
	}

	@Override
	public List<CertificationDTO> getMovieCertifications(AsyncType type) {
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
		return certificationDtos;
	}


	@Async
	@Override
	public CompletableFuture<List<MovieDTO>> getMovieListByType(MovieCategory category) {
		log.debug("Find CategoryMovieList. category={}", category);
		var krReleasedMovies = new ArrayList<TmdbMovieResponse>();
		
		var page = 1;
		while(krReleasedMovies.size() < 10) {
			var tmdbMoviesResponse = tmdbClient.getMovieList(TmdbMovieCategory.valueOf(category.getValue()), page).getBody();
			if(tmdbMoviesResponse == null || tmdbMoviesResponse.getResults() == null || tmdbMoviesResponse.getResults().isEmpty() ) {
				return CompletableFuture.completedFuture(Collections.emptyList());
			}
			getKrReleasedTmdbMovies(krReleasedMovies, tmdbMoviesResponse, 10);
			page++;
		}
		var typeMovieDtos = movieMapper.tmdbMoviesToMovieDto(krReleasedMovies);
		log.info("MovieList By Category Found. count={}", typeMovieDtos.size());
		return CompletableFuture.completedFuture(typeMovieDtos);
	}
	
	
//	public CompletableFuture<List<MovieDTO>> getMovieListByType(MovieCategory category) {
//		log.debug("Find CategoryMovieList. category={}", category);
//		var krReleasedMovies = new ArrayList<TmdbMovieResponse>();
//		
//		var page = 1;
//		while(krReleasedMovies.size() < 10) {
//			var tmdbMoviesResponse = tmdbClient.getMovieList(TmdbMovieCategory.valueOf(category.getValue()), page).getBody();
//			if(tmdbMoviesResponse == null || tmdbMoviesResponse.getResults() == null || tmdbMoviesResponse.getResults().isEmpty() ) {
//				return Collections.emptyList();
//			}
//			getKrReleasedTmdbMovies(krReleasedMovies, tmdbMoviesResponse);
//			page++;
//		}
//		var typeMovieDtos = movieMapper.tmdbMoviesToMovieDto(krReleasedMovies);
//		log.info("MovieList By Category Found. count={}", typeMovieDtos.size());
//		return typeMovieDtos;
//	}

	@Async
	@Override
	public CompletableFuture<List<MovieDTO>> getMovieListByTimeType(TimeType timeType) {
		log.debug("Find ovieListByTimeType. TimeType={}", timeType);
		
		var krReleasedMovies = new ArrayList<TmdbMovieResponse>();
		
		var tmdbMoviesResponse = tmdbClient.getMovieTrendList(timeType.getValue()).getBody();
		if(tmdbMoviesResponse == null || tmdbMoviesResponse.getResults() == null || tmdbMoviesResponse.getResults().isEmpty() ) {
			return CompletableFuture.completedFuture(Collections.emptyList());
		}
		getKrReleasedTmdbMovies(krReleasedMovies, tmdbMoviesResponse, 10);
		
		var timeTypeMovieDtos = movieMapper.tmdbMoviesToMovieDto(krReleasedMovies);
		log.info("MovieList By TimeType Found. count={}", timeTypeMovieDtos.size());
		return CompletableFuture.completedFuture(timeTypeMovieDtos);
	}

	@Override
	public List<ProviderDTO> getProviders(AsyncType type) {
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
		return providerDtos;
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
	
	@Async
	@Override
	public CompletableFuture<List<MovieDTO>> getMovieSimilarListByMovieId(String movieId) {
		log.debug("Find MovieSimilarList. movieId={}", movieId);
		
		var krReleasedMovies = new ArrayList<TmdbMovieResponse>();
		// TODO 결과 없을때 
		var movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
		
		var page = 1;
		while(krReleasedMovies.size() < 10) {
			var tmdbSimilarResponse = tmdbClient.getSimilarMovieListById(
					TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId())).build(), page)
					.getBody();
			
			if(tmdbSimilarResponse == null || tmdbSimilarResponse.getResults() == null || tmdbSimilarResponse.getResults().isEmpty() ) {
				return CompletableFuture.completedFuture(Collections.emptyList());
			}
			getKrReleasedTmdbMovies(krReleasedMovies, tmdbSimilarResponse, 10 );
			page++;
		}

		var similarMovieDtos = movieMapper.tmdbMoviesToMovieDto(krReleasedMovies);
		log.info("ProviderMovieList Found. movieId={}, count={}", movieId, similarMovieDtos.size());
		return CompletableFuture.completedFuture(similarMovieDtos);
		
		// TODO 중복처리 및 기존에 있는 값이면 처리
//		var movie = movieRepository.findById(movieId)
//					.orElseThrow(() -> new NetPickzException(ErrorCode.MOVIE_NOT_FOUND));
//		var tmdbSimilarMoviesResponse = tmdbClient.getSimilarMovieListById(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId())).build()).getBody();
//		if(tmdbSimilarMoviesResponse == null ) {
//			throw new NetPickzException(ErrorCode.TMDB_SIMILARLIST_NOT_FOUND);
//		}
//		var filterResult = tmdbSimilarMoviesResponse.getResults().stream().filter((e) -> !e.getOverview().isBlank()).toList();
	}

	@Async
	@Override
	public CompletableFuture<MovieListPageDTO> getMovieListBySearch(String title, int page) {
		log.debug("Find MovieList. title={}", title);
		
		var krReleasedMovies = new ArrayList<TmdbMovieResponse>();
		var totalCount = 0;
		var totalPage = 0;
		// TODO 레디스에다가 조회한 데이터 id 값 저장하기 + 중복 안되도록
		while(krReleasedMovies.size() < 12) {
			var tmdbMoviesResponse = tmdbClient.getMovieListBySearch(title,page).getBody();
			System.out.println(tmdbMoviesResponse.toString());
			totalCount = tmdbMoviesResponse.getTotalResults();
			totalPage = tmdbMoviesResponse.getTotalPages();
			if(tmdbMoviesResponse == null || tmdbMoviesResponse.getResults() == null || tmdbMoviesResponse.getResults().isEmpty()) {
				return CompletableFuture.completedFuture(null);// TODO
			}
			getKrReleasedTmdbMovies(krReleasedMovies, tmdbMoviesResponse, 12);
			page++;//TODO
		}
		// TODO 추후 vue 쪽에서 한 페이제 몇개 쩡도를 보여줄 지를 정한뒤 반환 리스트 크기 정해서 조절 필요
//		var filterData = fetchKrReleasedTmdbMovies(tmdbMoviesResponse);
		
		var movieDtos = movieMapper.tmdbMoviesToMovieDto(krReleasedMovies);
		log.info("MovieList Found. title={}, count={}", title, movieDtos.size());
		var movieListDtos = MovieListPageDTO.builder().movieDtos(movieDtos).totalPage(totalPage).totalCount(totalCount).build();
		log.info("MovieList Found. totalCount={}, totalPage={}, count={}", movieListDtos.getTotalCount(), movieListDtos.getTotalPage(), movieListDtos.getTotalCount());
		return CompletableFuture.completedFuture(movieListDtos);
//		return CompletableFuture.completedFuture(movieDtos);
	}

	@Async
	@Override
	public CompletableFuture<List<MovieDTO>> getMovieListByFilter(FilterRequest filterRequest) {
		log.debug("Find MovieList By Filter. filter={}", filterRequest.toString());
		
		var krReleasedMovies = new ArrayList<TmdbMovieResponse>();
		var page = 1;
		while(krReleasedMovies.size() < 10) {
			filterRequest.setPage(page); //  TODO 
			var tmdbMoviesResponse = tmdbClient.getMovieListByFilter(filterRequest).getBody();
			System.out.println("getTotalPages : " + tmdbMoviesResponse.getTotalPages());
			System.out.println("getTotalResults : " + tmdbMoviesResponse.getTotalResults() );
			if(tmdbMoviesResponse == null || tmdbMoviesResponse.getResults() == null || tmdbMoviesResponse.getResults().isEmpty()) {
				return CompletableFuture.completedFuture(Collections.emptyList());
			}
			getKrReleasedTmdbMovies(krReleasedMovies, tmdbMoviesResponse, 10 );
			page++;
		}
		
		// TODO 추후 vue 쪽에서 한 페이제 몇개 쩡도를 보여줄 지를 정한뒤 반환 리스트 크기 먼저 정하기 ㄱㄱ
		var movieDtos = movieMapper.tmdbMoviesToMovieDto(krReleasedMovies);
		log.info("MovieList Found. count={}", movieDtos.size());
		return CompletableFuture.completedFuture(movieDtos);
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

	@Override
	public List<MovieDTO> getMovieCertification(String id) {
		log.debug("get Certification. id={}", id);
		var tmdbResponse = tmdbClient.getReleaseDateList(TmdbMovieRequest.builder().movieId(Integer.valueOf(id)).build()).getBody();
		System.out.println(tmdbResponse);
		if(tmdbResponse == null) {
			throw new NetPickzException(ErrorCode.TMDB_MOVIE_NOT_FOUND); //
		}
		var res = tmdbResponse.getResults().stream().filter(e -> e.getCountryCode().equals("KR")).toList();
		
		var movieDtos = movieMapper.tmdbReleaseDateToMovieDto(res); // 
		log.info("movie certification Found. count={}", movieDtos.size());
		return movieDtos;
	}
	
	private List<TmdbMovieResponse> getKrReleasedTmdbMovies(List<TmdbMovieResponse> krReleasedMovies,
			TmdbMovieListResponse tmdbMoviesResponse , int size) {
		var filterData = fetchKrReleasedTmdbMovies(tmdbMoviesResponse);
		
		int extraSize = size - krReleasedMovies.size();
//		int extraSize = 10 - krReleasedMovies.size();
		krReleasedMovies.addAll(filterData.stream().limit(extraSize).toList());
		return krReleasedMovies;
	}

	private List<TmdbMovieResponse> fetchKrReleasedTmdbMovies(TmdbMovieListResponse tmdbMoviesResponse) {
		return tmdbMoviesResponse.getResults().stream()
			    .filter(movie -> {
			        var releaseRes = tmdbClient.getReleaseDateList(TmdbMovieRequest.builder().movieId(Integer.valueOf(movie.getId())).build()).getBody();
			        if (releaseRes == null) return false; 
			        
			        var krReleaseDate = releaseRes.getResults().stream()
		                    .filter(r -> "KR".equals(r.getCountryCode()))
		                    .flatMap(r -> r.getReleaseDates().stream())
//		                    .filter(rd -> rd.getType() == 3 || rd.getType() == 4)
		                    .findFirst();
			        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

		            krReleaseDate.ifPresent(rd -> movie.setReleaseDate(LocalDate.parse(rd.getReleaseDate(), formatter).toString())); // ✅ 날짜 세팅(TODO 추후 좀 더 체크 필요)
		            return krReleaseDate.isPresent(); // ✅ filter 조건rd.getReleaseDate()

			    })
			    .toList();
	}

}
