package com.netpickz.core.external.tmdb;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import reactor.core.publisher.Mono;

@Component
public class TmdbClient  {
	
	private final WebClient webClient;
	private String apiKey =  "849673b235e88d4045f4d45e77d2de71";
	
	public TmdbClient(WebClient.Builder builder) {
		String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NDk2NzNiMjM1ZTg4ZDQwNDVmNGQ0NWU3N2QyZGU3MSIsIm5iZiI6MTc1NzMxNDMzMS45NTEsInN1YiI6IjY4YmU3ZDFiNjRkMjc4Nzg5YjFmNTIwOSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.rD3ARw0LudH1ZQD-VkZdaeIeOHtCdFVgC2Tfa8-JcCo";
		this.webClient = builder.baseUrl("https://api.themoviedb.org/3")
//				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.build();
	}
	
	// session	
	public ResponseEntity<TmdbSessionResponse> createGuestSession() {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/authentication/guest_session/new")
					.queryParam(apiKey, apiKey)
					.build())
				.retrieve()
				.toEntity(TmdbSessionResponse.class)
				.block();
	} 
	
	// step1 
	public ResponseEntity<TmdbTokenResponse> createRequestToken() {
		return webClient
				.get()
				.uri("/authentication/token/new?api_key=849673b235e88d4045f4d45e77d2de71`")
				.retrieve()
				.toEntity(TmdbTokenResponse.class)
				.block();
	} 

	// step3 
	public ResponseEntity<TmdbSessionResponse> createSession() {
		return webClient
				.post()
				.uri("/authentication/session/new?api_key=849673b235e88d4045f4d45e77d2de71")
				.body(BodyInserters.fromValue(Map.of("request_token", "sdsdsdsdsgewggeg")))
				.retrieve()
				.toEntity(TmdbSessionResponse.class)
				.block();
	} 
	
	
	public ResponseEntity<TmdbSessionResponse> deleteSession() {
		return webClient
				.delete()
				.uri(uriBuilder -> uriBuilder
					.path("/authentication/session")
					.queryParam(apiKey, apiKey)
					.build())
//				.body(BodyInserters.fromValue(Map.of("session_id", "2629f70fb498edc263a0adb99118ac41f0053e8c")))
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
	
	public ResponseEntity<TmdbCertificationResponse> getCertificationList(){
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
					.path("/certification/movie/list")
					.queryParam("api_key", apiKey)
					.build())
				.retrieve()
				.toEntity(TmdbCertificationResponse.class)
				.block();
	}

	public ResponseEntity<TmdbMovieResponse> getMovieInfo() {
		return webClient
			    .get()
			    .uri(uriBuilder -> uriBuilder
					.path("/movie/{movie_id}")
					.queryParam("api_key", apiKey)
					.queryParam("language", "ko-KR")
					.build(1038392))
			    .retrieve()
			    .toEntity(TmdbMovieResponse.class)
			    .block();
	} 
	
	// 밑에 movieList 합쳐야함.
	public ResponseEntity<TmdbMovieListResponse> getNowPlayingMovieList() {
		return webClient
			    .get()
			    .uri("/movie/now_playing?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	} 
	
	public ResponseEntity<TmdbMovieListResponse> getPopularMovieList() {
		return webClient
			    .get()
			    .uri("/movie/popular?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
			    .retrieve()
			    .toEntity(TmdbMovieListResponse.class)
			    .block();
	}
	
	public ResponseEntity<TmdbMovieListResponse> getTopRatedMovieList() {
		return webClient
				.get()
				.uri("/movie/top_rated?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
				.retrieve()
				.toEntity(TmdbMovieListResponse.class)
				.block();
	} 
	
	public ResponseEntity<TmdbMovieListResponse> getUpcomingMovieList() {
		return webClient
				.get()
				.uri("/movie/upcoming?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
				.retrieve()
				.toEntity(TmdbMovieListResponse.class)
				.block();
	} 
	
	
	public ResponseEntity<TmdbMovieListResponse> getSimilarMovieListById() {
		return webClient
				.get()
				.uri("/movie/{movie_id}/similar?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
				.retrieve()
				.toEntity(TmdbMovieListResponse.class)
				.block();
	} 
	
	public ResponseEntity<TmdbProviderResponse> getWatchProviderList() {
		return webClient
				.get()
				.uri("/movie/{movie_id}/watch/providers?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
				.retrieve()
				.toEntity(TmdbProviderResponse.class)
				.block();
	} 
	
	public ResponseEntity<TmdbProviderResponse> addRating() {
		return webClient
				.post()
				.uri("/movie/{movie_id}/rating?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
				.body(BodyInserters.fromValue(Map.of("value", "6.5")))
				.retrieve()
				.toEntity(TmdbProviderResponse.class)
				.block();
	} 

	public ResponseEntity<TmdbProviderResponse> deleteRating() {
		//guest_session_id or seesion Id
		return webClient
				.delete()
				.uri("/movie/{movie_id}/rating?api_key=849673b235e88d4045f4d45e77d2de71&language=ko-KR")
				.retrieve()
				.toEntity(TmdbProviderResponse.class)
				.block();
	} 
	
}
