package com.netpickz.core.external.tmdb;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.netpickz.api.movie.request.FilterRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbClient  {

	private final WebClient webClient;
    @Value("${tmdb.api.key}")
	private String apiKey;
	
	// session	
	public ResponseEntity<TmdbSessionResponse> createGuestSession() {
		System.out.println("apiKey" + apiKey);
		log.info("apiKey    =========== " + apiKey);
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
	public ResponseEntity<TmdbMovieListResponse> getMovieList(TmdbMovieCategory tmdbMovieCategory, int page) {
		return webClient
			    .method(HttpMethod.GET)
			    .uri(uriBuilder -> uriBuilder
			    	.path(tmdbMovieCategory.getPath())
			    	.queryParam("api_key", apiKey)
			    	.queryParam("page", page)
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
			    		.queryParam("page", filterRequest.getPage())
			    		.queryParam("release_date.lte", LocalDate.now())
			    		.queryParam("include_adult", filterRequest.getIncludeAdult())
			    		.queryParam("sort_by", filterRequest.getSortType().getValue());
			    		
			    		Optional.ofNullable(filterRequest.getWithGenres())
			    		.ifPresent(e -> uriBuilder.queryParam("with_genres", e));
			    		Optional.ofNullable(filterRequest.getWithPeople())
			    		.ifPresent(e -> uriBuilder.queryParam("with_people", e));
			    		Optional.ofNullable(filterRequest.getCertification())
			    		.ifPresent(e -> uriBuilder.queryParam("certification", e));
			    		Optional.ofNullable(filterRequest.getCertificationGte())
			    		.ifPresent(e -> uriBuilder.queryParam("certification.gte", e));
			    		Optional.ofNullable(filterRequest.getCertificationLte())
			    		.ifPresent(e -> uriBuilder.queryParam("certification.lte", e));
			    		Optional.ofNullable(filterRequest.getCertificationCountry())
			    		.ifPresent(e -> uriBuilder.queryParam("certification_country", e));
			    		
			    		return uriBuilder.build();
		    		})
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	}
	
	public ResponseEntity<TmdbMovieListResponse> getMovieListBySearch(String title, int page) {
		return webClient
			    .method(HttpMethod.GET)
			    .uri(uriBuilder -> uriBuilder
			    	.path("/search/movie")
			    	.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.queryParam("region", "KR")
					.queryParam("query", title)
					.queryParam("page", page)
					.build()
		    		)
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	}
	
	public ResponseEntity<TmdbMovieListResponse> getSimilarMovieListById(TmdbMovieRequest request, int page) {
		return webClient
				.method(HttpMethod.GET)
				.uri(uriBuilder -> uriBuilder
				    	.path("/movie/{movie_id}/similar")
				    	.queryParam("api_key", apiKey)
						.queryParam("language", "ko-KR")
						.queryParam("page", page)
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
						.queryParam("guest_session_id", request.getSessionId())
						.build(request.getMovieId()))
				.retrieve()
				.toEntity(TmdbRatingResponse.class)
				.block();
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
	
	// TODO Release Dates
	public ResponseEntity<TmdbMovieReleaseDateListResponse> getReleaseDateList(TmdbMovieRequest request) {
		return webClient
				.method(HttpMethod.GET)
				.uri(uriBuilder -> uriBuilder
			    	.path("/movie/{movie_id}/release_dates")
			    	.queryParam("api_key", apiKey)
					.build(request.getMovieId()))
				.retrieve()
				.toEntity(TmdbMovieReleaseDateListResponse.class)
				.block();
	} 
	
	
}
