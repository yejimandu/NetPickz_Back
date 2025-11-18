package com.netpickz.core.external.tmdb;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.netpickz.api.movie.request.FilterRequest;

@Component
public class TmdbClient  {

	private final WebClient webClient;
	private String apiKey =  "849673b235e88d4045f4d45e77d2de71";
	
	public TmdbClient(WebClient.Builder builder) {
		this.webClient = builder.baseUrl("https://api.themoviedb.org/3")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.build();
	}
	
	// session	
	public ResponseEntity<TmdbSessionResponse> createGuestSession() {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/authentication/guest_session/new")
					.queryParam("api_key", apiKey)
					.build())
				.retrieve()
				.toEntity(TmdbSessionResponse.class)
				.block();
	} 
	
	// step1 
	public ResponseEntity<TmdbTokenResponse> createRequestToken() {
		return webClient
				.method(HttpMethod.GET)
				.uri(uriBuilder -> uriBuilder
						.path("/authentication/token/new")
						.queryParam("api_key", apiKey)
						.build())
				.retrieve()
				.toEntity(TmdbTokenResponse.class)
				.block();
	} 

	// step3 
	public ResponseEntity<TmdbSessionResponse> createSession(TmdbSessionRequest request) {
		return webClient
				.method(HttpMethod.POST)
				.uri(uriBuilder -> uriBuilder
					.path("/authentication/session/new")
					.queryParam("api_key", apiKey)
					.build()
					)
				.body(BodyInserters.fromValue(Map.of("request_token", request.getRequestToken())))
				.retrieve()
				.toEntity(TmdbSessionResponse.class)
				.block();
	} 
	
	
	public ResponseEntity<TmdbSessionResponse> deleteSession(TmdbSessionRequest request) {
		return webClient
				.method(HttpMethod.DELETE)
				.uri(uriBuilder -> uriBuilder
					.path("/authentication/session")
					.queryParam("api_key", apiKey)
					.build())
				.body(BodyInserters.fromValue(Map.of("session_id", request.getSessionId())))
				.retrieve()
				.toEntity(TmdbSessionResponse.class)
				.block();
	} 
	
	
	public ResponseEntity<TmdbGenreListResponse> getGenreList(){
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/genre/movie/list")
					.queryParam("api_key", apiKey)
					.queryParam("language", "ko")
					.build())
				.retrieve()
				.toEntity(TmdbGenreListResponse.class)
				.block();
	}
	
	public ResponseEntity<TmdbCertificationListResponse> getCertificationList(){
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/certification/movie/list")
					.queryParam("api_key", apiKey)
					.build())
				.retrieve()
				.toEntity(TmdbCertificationListResponse.class)
				.block();
	}
	
	public ResponseEntity<TmdbProviderListResponse> getProviderList(){
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/watch/providers/movie")
					.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.queryParam("watch_region", "KR")
					.build())
				.retrieve()
				.toEntity(TmdbProviderListResponse.class)
				.block();
	}

	public ResponseEntity<TmdbMovieResponse> getMovieInfo(TmdbMovieRequest request) {
		return webClient
			    .get()
			    .uri(uriBuilder -> uriBuilder
					.path("/movie/{movie_id}")
					.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.build(request.getMovieId()))
			    .retrieve()
			    .toEntity(TmdbMovieResponse.class)
			    .block();
	} 
	
	
	// /movie/now_playing 
			// /movie/popular
			// /movie/top_rated
			// /movie/upcoming
	public ResponseEntity<TmdbMovieListResponse> getMovieList(TmdbMovieCategory tmdbMovieCategory) {
		return webClient
			    .method(HttpMethod.GET)
			    .uri(uriBuilder -> uriBuilder
			    	.path(tmdbMovieCategory.getPath())
			    	.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.build()
		    		)
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	}
	
	public ResponseEntity<TmdbMovieListResponse> getMovieTrendList(String timeType) {
		return webClient
			    .method(HttpMethod.GET)
			    .uri(uriBuilder -> uriBuilder
			    	.path("/trending/movie/{time_window}")
			    	.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.build(timeType)
		    		)
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	}
	
	public ResponseEntity<TmdbMovieListResponse> getMovieListByFilter(FilterRequest filterRequest) {
		return webClient
			    .method(HttpMethod.GET)
			    .uri(uriBuilder -> {
			    		uriBuilder
			    		.path("/discover/movie")
			    		.queryParam("api_key", apiKey)
			    		.queryParam("language", "ko-KR")
			    		.queryParam("region", "KR")
			    		.queryParam("with_release_type", "2|3|4|5")
			    		.queryParam("release_date.lte", LocalDate.now())
			    		.queryParam("include_adult", filterRequest.getIncludeAdult())
			    		.queryParam("sort_by", filterRequest.getSortType().getValue());
			    		
			    		Optional.ofNullable(filterRequest.getWithGenres())
			    		.ifPresent(e -> uriBuilder.queryParam("with_genres", e));
			    		Optional.ofNullable(filterRequest.getWithPeople())
			    		.ifPresent(e -> uriBuilder.queryParam("with_people", e));
			    		
			    		return uriBuilder.build();
		    		})
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	}
	
	public ResponseEntity<TmdbMovieListResponse> getMovieListBySearch(String title) {
		return webClient
			    .method(HttpMethod.GET)
			    .uri(uriBuilder -> uriBuilder
			    	.path("/search/movie")
			    	.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.queryParam("region", "KR")
					.queryParam("query", title)
					.queryParam("page", 1)
					.build()
		    		)
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	}
	
	public ResponseEntity<TmdbMovieListResponse> getSimilarMovieListById(TmdbMovieRequest request) {
		return webClient
				.method(HttpMethod.GET)
				.uri(uriBuilder -> uriBuilder
				    	.path("/movie/{movie_id}/similar")
				    	.queryParam("api_key", apiKey)
						.queryParam("language", "ko-KR")
						.build(request.getMovieId())
			    		)
				.retrieve()
				.toEntity(TmdbMovieListResponse.class)
				.block();
	} 
	
	public ResponseEntity<TmdbWatchProviderListResponse> getWatchProviderList(TmdbMovieRequest request) {
		return webClient
				.method(HttpMethod.GET)
				.uri(uriBuilder -> uriBuilder
			    	.path("/movie/{movie_id}/watch/providers")
			    	.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.build(request.getMovieId()))
				.retrieve()
				.toEntity(TmdbWatchProviderListResponse.class)
				.block();
	} 
	
	public ResponseEntity<TmdbRatingResponse> addRating(TmdbMovieRequest request) {
		return webClient
				.method(HttpMethod.POST)
				.uri(uriBuilder -> uriBuilder
						.path("/movie/{movie_id}/rating")
						.queryParam("api_key", apiKey)
						.queryParam("guest_session_id", request.getSessionId())
						.build(request.getMovieId()))
				.bodyValue(Map.of("value", request.getRating()))
				.retrieve()
				.toEntity(TmdbRatingResponse.class)
				.block();
	} 

	public ResponseEntity<TmdbRatingResponse> deleteRating(TmdbMovieRequest request) {
		//guest_session_id or seesion Id
		return webClient
				.method(HttpMethod.DELETE)
				.uri(uriBuilder -> uriBuilder
						.path("/movie/{movie_id}/rating")
						.queryParam("api_key", apiKey)
						.queryParam("language", "ko-KR")
						.build(request.getMovieId()))
				.bodyValue(Map.of("guest_session_id", request.getGuestSessionId()))
				.retrieve()
				.toEntity(TmdbRatingResponse.class)
				.block();
				//				.body(BodyInserters.fromValue(Map.of("session_id", request.getSessionId())))
	} 
	
	public ResponseEntity<TmdbMovieResponse> ratingListByUser(TmdbMovieRequest request) {
		return webClient
				.method(HttpMethod.DELETE)
				.uri(uriBuilder -> uriBuilder
						.path("/account/{account_id}/rated/movies")
						.queryParam("api_key", apiKey)
						.queryParam("language", "ko-KR")
						.queryParam("page", request.getPage() )
						.queryParam("session_id", request.getSessionId() )
						.queryParam("sort_by", "created_at.desc" )
						.build(1038392))
//				.body(BodyInserters.fromValue(Map.of()))
				.retrieve()
				.toEntity(TmdbMovieResponse.class)
				.block();
	} 
	
}
