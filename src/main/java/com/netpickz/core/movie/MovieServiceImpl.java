package com.netpickz.core.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.netpickz.common.enumType.MovieCategory;
import com.netpickz.common.enumType.TimeType;
import com.netpickz.common.repository.CertificationRepository;
import com.netpickz.common.repository.GenreRepository;
import com.netpickz.common.repository.ProvidersRepository;
import com.netpickz.common.util.IdGenerator;
import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbMovieCategory;
import com.netpickz.core.external.tmdb.TmdbMovieRequest;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.movie.entity.MovieInfoEntity;
import com.netpickz.core.movie.entity.MovieProviderEntity;
import com.netpickz.core.movie.repository.MovieInfoRepository;
import com.netpickz.core.movie.repository.MovieProviderRepository;
import com.netpickz.core.movie.repository.MovieRepository;
import com.netpickz.core.movie.repository.MovieRepositoryCustom;
import com.netpickz.core.user.UserDTO;
import com.netpickz.core.user.UserService;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private MovieRepositoryCustom movieRepositoryCustom;
	
	@Autowired
	private MovieInfoRepository movieInfoRepository;
	
	@Autowired
	private GenreRepository genreRepository;
	
	@Autowired
	private CertificationRepository certificationRepository;
	
	@Autowired
	private ProvidersRepository providersRepository;
	
	@Autowired
	private MovieProviderRepository movieProviderRepository;;
	
	@Autowired
	private TmdbClient tmdbClient;
	
	@Autowired
	private UserService userService;

	
	@Override
	public Optional<MovieDTO> getMovieInfoByExternalId(String id) {
		var movieID = IdGenerator.getId("mv");
		// TODO
		// 1. 해당 아이디가 DB에 존재하는지 체크
 		// async 값 기준으로 true 인경우만 tmdb api 호출하고 아닌 경우는 db 조회
		var tmdbMovieResponse = tmdbClient.getMovieInfo(TmdbMovieRequest.builder().movieId(Integer.valueOf(id)).build()).getBody();
		var movieEntity = MovieEntity.builder().id(String.valueOf(tmdbMovieResponse.getId())).title(tmdbMovieResponse.getTitle()).movieId(movieID).build();
		//TODO
		movieRepository.save(movieEntity);
		var movieInfoEntity = MovieInfoEntity.builder().movieId(movieID).overView(tmdbMovieResponse.getOverview()).posterPath(tmdbMovieResponse.getPosterPath())
				.releaseDate(tmdbMovieResponse.getReleaseDate()).build();
		movieInfoRepository.save(movieInfoEntity);
		
		var movieDTo = Optional.of( MovieDTO.builder().movieId(movieID).posterPath(tmdbMovieResponse.getPosterPath()).releaseDate(tmdbMovieResponse.getReleaseDate())
				.overview(tmdbMovieResponse.getOverview()).runtime(tmdbMovieResponse.getRuntime()).title(tmdbMovieResponse.getTitle())
				.id(tmdbMovieResponse.getId()).build());
		
		return  movieDTo;
	}

	@Override
	public Optional<MovieDTO> getMovieInfoByMovieIdAndType(String movieId, AsyncType type) {
		
		if(Boolean.valueOf(type.getValue())) {
			var tmdbMovieResponse = tmdbClient.getMovieInfo(TmdbMovieRequest.builder().movieId(Integer.valueOf(movieId)).build()).getBody();
			var movieEntity = MovieEntity.builder().id(String.valueOf(tmdbMovieResponse.getId())).title(tmdbMovieResponse.getTitle()).movieId(movieId).build();
			movieRepository.save(movieEntity);
			var movieInfoEntity = MovieInfoEntity.builder().movieId(movieId).overView(tmdbMovieResponse.getOverview()).posterPath(tmdbMovieResponse.getPosterPath())
					.releaseDate(tmdbMovieResponse.getReleaseDate()).build();
			movieInfoRepository.save(movieInfoEntity);
		}
		
		// TODO
		// 1. 해당 아이디가 DB에 존재하는지 체크
 		// async 값 기준으로 true 인경우만 tmdb api 호출하고 아닌 경우는 db 조회
		var movieEntitys =  movieRepository.findByMovieId(movieId).get();
		var movieInfoEntitys =  movieInfoRepository.findById(movieId).get();
		var movieDTo = MovieDTO.builder().movieId(movieId).posterPath(movieInfoEntitys.getPosterPath()).releaseDate(movieInfoEntitys.getReleaseDate())
				.overview(movieInfoEntitys.getOverView())
//				.runtime(movieInfoEntitys.get)
				.title(movieEntitys.getTitle())
				.movieId(movieId)
				.id(Integer.valueOf(movieEntitys.getId())).build();
		
		return  Optional.of(movieDTo);
	}

	@Override
	public Optional<List<GenreDTO>> getMovieGenres(AsyncType type) {
		if(Boolean.valueOf(type.getValue())) {
			var tmdbGenreResponse =  tmdbClient.getGenreList().getBody();
			var genreEntity = tmdbGenreResponse.getGenres().stream().map(re -> GenreEntity.builder().id( re.getId()).name(re.getName()).build()).toList();
			genreRepository.saveAll(genreEntity);
		}
		var genres = genreRepository.findAll();
		var genreDtos = genres.stream().map(e -> new GenreDTO(e.getId() , e.getName())).toList();
		return Optional.of(genreDtos);
	}


	@Override
	public Optional<List<CertificationDTO>> getMovieCertifications(AsyncType type) {
		if(Boolean.valueOf(type.getValue())) {
			var tmdbCetificationResponse =  tmdbClient.getCertificationList().getBody();
			var certificationEntitys = tmdbCetificationResponse.getCertifications().get("KR").stream().map(re -> CertificationEntity.builder().certification(re.getCertification()).meaning(re.getMeaning()).orderNum(re.getOrder()).build()).toList(); 
			System.out.println(certificationEntitys);
			certificationRepository.saveAll(certificationEntitys);
		}
		var certifications = certificationRepository.findAll();
		var certificationDtos = certifications.stream().map(e -> new CertificationDTO(e.getCertification(), e.getMeaning(), e.getOrderNum())).toList();
		return Optional.of(certificationDtos);
	}


	@Override
	public Optional<List<MovieDTO>> getMovieListByType(MovieCategory category) {
//		if(Boolean.valueOf(type.getValue())) {
			var tmdbMoviesResponse = tmdbClient.getMovieList(TmdbMovieCategory.valueOf(category.getValue())).getBody();
			var typeMovieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
					.posterPath(e.getPosterPath()).overview(e.getOverview())
					.releaseDate(e.getReleaseDate()).genres(e.getGenres())
					.originalLanguage(e.getOriginalLanguage())
					.title(e.getTitle()).id(e.getId()).build())
					.toList();
//			var movieId = IdGenerator.getId("mv_");
//			var movieEntitys = result.stream().map(re -> MovieEntity.builder().id(String.valueOf(re.getId())).movieId(movieId)
//					.title(re.getTitle()).build()).toList();
//			movieRepository.saveAll(movieEntitys);
//			
//			var movieInfoEntitys = result.stream().map(re -> MovieInfoEntity.builder().movieId(movieId).overView(re.getOverview())
//					.posterPath(re.getPosterPath()).releaseDate(re.getReleaseDate()).build()).toList();
//			movieInfoRepository.saveAll(movieInfoEntitys);
//		}
//		movieRepositoryCustom.findAllByCategory(category);
		return Optional.of(typeMovieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListByTimeType(TimeType timeType) {
		var tmdbMoviesResponse = tmdbClient.getMovieTrendList(timeType.getValue()).getBody();
		var timeTypeMovieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
				.posterPath(e.getPosterPath()).overview(e.getOverview())
				.releaseDate(e.getReleaseDate()).genres(e.getGenres())
				.originalLanguage(e.getOriginalLanguage())
				.title(e.getTitle()).id(e.getId()).build())
				.toList();
		
		return Optional.of(timeTypeMovieDtos);
	}

	@Override
	public Optional<List<ProviderDTO>> getProviders(AsyncType type) {
		if(Boolean.valueOf(type.getValue())) {
			var tmdbProviderResponse =  tmdbClient.getProviderList().getBody();
			var providerEntitys = tmdbProviderResponse.getResults().stream().map(re -> ProvidersEntity.builder()
					.id(re.getProviderId()).name(re.getProviderName())
					.logoPath(re.getLogoPath()).orderNum(String.valueOf(re.getDisplayPriority()))
					.build()).toList();
			providersRepository.saveAll(providerEntitys);
		}
		var providers = providersRepository.findAll();
		var providerDtos = providers.stream().map(e -> new ProviderDTO(e.getId(), e.getName(),e.getLogoPath(), e.getOrderNum())).toList();
		return Optional.of(providerDtos);
	}


	@Override
	public Optional<List<MovieDTO>> getProviderByMovieId(String movieId) {
		var tmdbMovie = movieRepository.findByMovieId(movieId).get();
		var tmdbProviderByMovieIdResponse = tmdbClient.getWatchProviderList(TmdbMovieRequest.builder().movieId(Integer.valueOf(tmdbMovie.getId())).build()).getBody();
		
		var movieProviderEntity = tmdbProviderByMovieIdResponse.getResults().get("KR").getFlatrate().stream().map(re -> MovieProviderEntity.builder().movieEntity(MovieEntity.builder().movieId(movieId).build())
				.providersEntity(ProvidersEntity.builder().id(re.getProviderId()).build()).build()).toList();
		
		var movieProviders =  movieProviderRepository.saveAll(movieProviderEntity);
		var movieDtos = movieProviders.stream().map(e -> new MovieDTO().builder().movieId(e.getMovieEntity().getMovieId()).providerId(e.getProvidersEntity().getId()).build()).toList();
		return Optional.of(movieDtos);
	}


	@Override
	public Optional<List<MovieDTO>> getMovieSimilarListByMovieId(String movieId) {
		var tmdbMovie = movieRepository.findByMovieId(movieId).get();
		var tmdbSimilarMoviesResponse = tmdbClient.getSimilarMovieListById(TmdbMovieRequest.builder().movieId(Integer.valueOf(tmdbMovie.getId())).build()).getBody();
		var similarMovieDtos = tmdbSimilarMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder().posterPath(e.getPosterPath()).overview(e.getOverview())
				.releaseDate(e.getReleaseDate()).title(e.getTitle()).id(e.getId()).build()).toList();
		return Optional.of(similarMovieDtos);
	}


	@Override
	public void addRatingByUserId(String movieId, RatingRequest ratingRequest) {
		var tmdbMovie = movieRepository.findByMovieId(movieId).get();
		var ddd = tmdbClient.addRating(TmdbMovieRequest.builder().movieId(Integer.valueOf(tmdbMovie.getId()))
				.sessionId(ratingRequest.getSessionId()).rating(ratingRequest.getValue()).build());
		System.out.println(ddd);
		//TODO
//		userService.addRatingByUser(UserDTO.builder().sessionId(ratingRequest.getSessionId()).build(), ratingRequest.getValue());
		
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListBySearch(String title) {
		var tmdbMoviesResponse = tmdbClient.getMovieListBySearch(title).getBody();
		var timeTypeMovieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
				.posterPath(e.getPosterPath()).overview(e.getOverview())
				.releaseDate(e.getReleaseDate()).genres(e.getGenres())
				.originalLanguage(e.getOriginalLanguage())
				.title(e.getTitle()).id(e.getId()).build())
				.toList();
		
		return Optional.of(timeTypeMovieDtos);
	}

	@Override
	public Optional<List<MovieDTO>> getMovieListByFilter(FilterRequest filterRequest) {
		var tmdbMoviesResponse = tmdbClient.getMovieListByFilter(filterRequest).getBody();
		var timeTypeMovieDtos = tmdbMoviesResponse.getResults().stream().map(e -> new MovieDTO().builder()
				.posterPath(e.getPosterPath()).overview(e.getOverview())
				.releaseDate(e.getReleaseDate()).genres(e.getGenres())
				.originalLanguage(e.getOriginalLanguage())
				.title(e.getTitle()).id(e.getId()).build())
				.toList();
		
		return Optional.of(timeTypeMovieDtos);
	}

		
}
