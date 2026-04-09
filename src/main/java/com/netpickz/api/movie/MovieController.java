package com.netpickz.api.movie;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.netpickz.core.movie.service.MovieService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173") //
@RequiredArgsConstructor
@RestController
@RequestMapping("/movies")
@Tag(name = "Movies", description = "영화 관련 기능을 제공하는 컨트롤러입니다.")
public class MovieController {

	private final MovieService movieService;

	@Operation(summary = "영화 정보 조회", description = "Tmdb 영화 ID 기준으로 영화 정보를 조회합니다.")
    @Parameter(name = "id", required = true, description = "TMDB 영화 고유 ID")
	@ApiResponses(value = {
			@ApiResponse(description = "에러 설명", responseCode = "500" ),
			@ApiResponse( responseCode = "200",
				    description = "따봉"
					)
	})
	@GetMapping("/external/{id}")
	public ResponseEntity<MovieDTO> getMovieInfoByExternalId(
			@PathVariable(name = "id") String id) {
		var movieDTO =  movieService.getMovieInfoByExternalId(id);
		return ResponseEntity.status(HttpStatus.OK).body(movieDTO.orElse(null));
	}
	
	@Operation(summary = "영화 정보 조회", description = "영화 아이디 기준으로 영화 정보를 조회합니다.")
    @Parameter(name = "movieId", required = true, description = "영화 ID")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/{movieId}")
	public ResponseEntity<MovieDTO> getMovieInfo(
			@PathVariable(name = "movieId") String movieId, 
			@RequestParam(name = "asynType") AsyncType type) {
		var movieDTO =  movieService.getMovieInfoByMovieIdAndType(movieId, type);
		return ResponseEntity.status(HttpStatus.OK).body(movieDTO.orElse(null));
	}
	
	// TODO 영화 관람등급 및 출시일 날 조회 api ( 추후 getMovieInfo api랑 합칠 계획)
	@Operation(summary = "특정 영화 관람등급 조회", description = "영화 ID 기준으로 해당 영화의 관람 등급을 조회합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@GetMapping("/{id}/certification")
	public ResponseEntity<List<MovieDTO>> getMovieCertificationByMovieId(   
			@PathVariable(name = "id") String id) {
		var movieDTOs = movieService.getMovieCertification(id);
		return ResponseEntity.status(HttpStatus.OK).body(movieDTOs);
	}
	
	
	@Operation(summary = "타입별 영화 목록 조회", description = "원하는 영화 목록을 조회합니다.")
	@Parameter(name = "category" , description = "영화 카테고리")
	@GetMapping("/category")
	public CompletableFuture<ResponseEntity<List<MovieDTO>>> getMovieList(   
		    @RequestParam(name = "category") MovieCategory category) {
		 return movieService.getMovieListByType(category)
			        .thenApply(ResponseEntity::ok);
	}
	
	@Operation(summary = "일간, 주간 기준으로 인기 영화 목록 조회", description = "일간, 주간 기준으로 원하는 영화 목록을 조회합니다.")
	@Parameter(name = "type" , description = "시간기준")
	@GetMapping("/trending")
	public CompletableFuture<ResponseEntity<List<MovieDTO>>> getMovieListByTime(   
		    @RequestParam(name = "type") TimeType timeType) {
		return movieService.getMovieListByTimeType(timeType)
				.thenApply(ResponseEntity::ok);
	}
	
	@Operation(summary = "영화 제공업자 목록 조회", description = "원하는 영화를 시청할 수 있는 OTT 목록을 조회합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@GetMapping("/{movieId}/providers")
	public ResponseEntity<List<MovieDTO>> getMovieProviderList(   
			@PathVariable(name = "movieId") String movieId) {
		var movieDTOs = movieService.getProviderByMovieId(movieId);
		return ResponseEntity.ok(movieDTOs);
	}
	
	@Operation(summary = "비슷한 영화 목록 조회", description = "영화 ID 기준으로 비슷한 영화 목록을 조회합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@GetMapping("/{movieId}/similar")
	public CompletableFuture<ResponseEntity<List<MovieDTO>>> getMovieSimilarListByMovieId(   
			@PathVariable(name = "movieId") String movieId) {
		return movieService.getMovieSimilarListByMovieId(movieId)
				.thenApply(ResponseEntity::ok);
	}
	
	@Operation(summary = "영화 장르 목록 조회", description = "영화 장르 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/genres")
	public ResponseEntity<List<GenreDTO>> getGenreList(
			@RequestParam(name = "asynType") AsyncType type) {
		var genres = movieService.getMovieGenres(type);
		return ResponseEntity.status(HttpStatus.OK).body(genres);
	}
	
	@Operation(summary = "영화 관람 등급 목록 조회", description = "영화 관람 등급 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/certifications")
	public ResponseEntity<List<CertificationDTO>> getCertificationList(
			@RequestParam(name = "asynType") AsyncType type) {
		var certifications = movieService.getMovieCertifications(type);
		return ResponseEntity.status(HttpStatus.OK).body(certifications);
	}
	
	@Operation(summary = "영화 제공업자 목록 조회", description = "영화 제공업자 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/providers")
	public ResponseEntity<List<ProviderDTO>> getProviderList(
			@RequestParam(name = "asynType") AsyncType type) {
		var providers = movieService.getProviders(type);
		return ResponseEntity.status(HttpStatus.OK).body(providers);
	}
	
	// TODO
	@Operation(summary = "영화 평가 등록", description = "사용자 ID 기준으로 영화 평가를 등록합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@PostMapping("/{movieId}/rating")
	public ResponseEntity<RatingDTO> addRating(
			@PathVariable(name="movieId") String movieId,
			@org.springframework.web.bind.annotation.RequestBody RatingRequest request) {
		// TODO
		var rationDto =  movieService.addRatingByUserId(movieId, request);
		return ResponseEntity.status(HttpStatus.OK).body(rationDto.orElse(null));
	}

	@Operation(summary = "영화 평가 조회", description = "사용자 ID 기준으로 영화 평가를 조회합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@GetMapping("/{movieId}/rating")
	public ResponseEntity<RatingDTO> getRating(
			@PathVariable(name="movieId") String movieId, Authentication authentication) {
		// TODO
		var userId = authentication.getName();
		var rationDto = movieService.getRatingByUserId(movieId, userId);
		return ResponseEntity.status(HttpStatus.OK).body(rationDto.orElse(null));
	}
	
	@Operation(summary = "영화 평가 삭제", description = "사용자 ID 기준으로 영화 평가를 삭제합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@DeleteMapping("/{movieId}/rating")
	public ResponseEntity<Boolean> deleteRating(
			@PathVariable(name = "movieId") String movieId,
			Authentication authentication) {
		var userId = authentication.getName();
		var flag =  movieService.deleteRatingByUserId(movieId, userId);
		return  new ResponseEntity<>(flag, HttpStatus.OK);
	}
	
    @Operation(summary = "영화 간단 검색", description = "영화 제목 기준으로 영화를 검색합니다.")
    @Parameter(name = "title", required = true, description = "영화 제목")
    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<MovieListPageDTO>> getSearchList(
    		@RequestParam(name = "title") String title, @RequestParam(name = "page") int page) {
    	return movieService.getMovieListBySearch(title, page)
    			.thenApply(ResponseEntity::ok);
    }
    
	@Operation(summary = "영화 필터 검색", description = "다양한 필터 기준으로 영화를 검색합니다.")
	@PostMapping("/search/multi")
	public CompletableFuture<ResponseEntity<List<MovieDTO>>> getSearchListTypeFilter(
			@org.springframework.web.bind.annotation.RequestBody FilterRequest request) {
		return movieService.getMovieListByFilter(request)
				.thenApply(ResponseEntity::ok);
	}
	
	

}
