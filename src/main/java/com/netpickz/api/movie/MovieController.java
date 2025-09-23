package com.netpickz.api.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.netpickz.api.login.LoginController;
import com.netpickz.api.request.RatingRequest;
import com.netpickz.common.dto.CertificationDTO;
import com.netpickz.common.dto.GenreDTO;
import com.netpickz.common.dto.ProviderDTO;
import com.netpickz.common.enumType.AsyncType;
import com.netpickz.common.enumType.MovieCategory;
import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbGenreListResponse;
import com.netpickz.core.external.tmdb.TmdbMovieResponse;
import com.netpickz.core.movie.MovieDTO;
import com.netpickz.core.movie.MovieService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/movies")
@Tag(name = "Movies", description = "영화 관련 기능을 제공하는 컨트롤러입니다.")
public class MovieController {

	@Autowired
	@Lazy
	private MovieService movieService;

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
		return  new ResponseEntity<MovieDTO>(movieDTO.isPresent() ? movieDTO.get() : null , HttpStatus.OK);
	}
	
	@Operation(summary = "영화 정보 조회", description = "영화 아이디 기준으로 영화 정보를 조회합니다.")
    @Parameter(name = "movieId", required = true, description = "영화 ID")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/{movieId}")
	public ResponseEntity<MovieDTO> getMovieInfo(
			@PathVariable(name = "movieId") String movieId, @RequestParam(name = "asynType") AsyncType type) {
		//TODO 파람 값 고민..
		var movieDTO =  movieService.getMovieInfoByMovieIdAndType(movieId, type);
		return  new ResponseEntity<MovieDTO>(movieDTO.isPresent() ? movieDTO.get() : null , HttpStatus.OK);
	}
	
	@Operation(summary = "타입별 영화 목록 조회", description = "원하는 영화 목록을 조회합니다.")
	@Parameter(name = "category" , description = "영화 카테고리")
	@GetMapping("")
	public ResponseEntity<List<MovieDTO>> getMovieList(   
		    @RequestParam(name = "category") MovieCategory category) {
		var movieDTOs =  movieService.getMovieListByType(category);
		return  new ResponseEntity<List<MovieDTO>>(movieDTOs.isPresent()? movieDTOs.get() : null, HttpStatus.OK);
	}
	
	@Operation(summary = "영화 제공업자 목록 조회", description = "원하는 영화를 시청할 수 있는 OTT 목록을 조회합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@GetMapping("{movieId}/providers")
	public ResponseEntity<List<MovieDTO>> getMovieProviderList(   
			@PathVariable(name = "movieId") String movieId) {
		var movieDtos = movieService.getProviderByMovieId(movieId);
		return  new ResponseEntity<List<MovieDTO>>(movieDtos.isPresent() ? movieDtos.get() : null, HttpStatus.OK);
	}
	@Operation(summary = "비슷한 영화 목록 조회", description = "영화 ID 기준으로 비슷한 영화 목록을 조회합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@GetMapping("/{movieId}/similar")
	public ResponseEntity<List<MovieDTO>> getMovieSimilarListByMovieId(   
			@PathVariable(name = "movieId") String movieId) {
		var similarMovies = movieService.getMovieSimilarListByMovieId(movieId);
		return  new ResponseEntity<List<MovieDTO>>(similarMovies.isPresent() ? similarMovies.get() : null, HttpStatus.OK);
	}
	
	@Operation(summary = "영화 장르 목록 조회", description = "영화 장르 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/genres")
	public ResponseEntity<List<GenreDTO>> getGenreList(
			@RequestParam(name = "asynType") AsyncType type) {
		var genres = movieService.getMovieGenres(type);
		return  new ResponseEntity<List<GenreDTO>>(genres.isPresent() ? genres.get() : null, HttpStatus.OK);
	}
	
	@Operation(summary = "영화 관람 등급 목록 조회", description = "영화 관람 등급 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/certifications")
	public ResponseEntity<List<CertificationDTO>> getCertificationList(
			@RequestParam(name = "asynType") AsyncType type) {
		var certifications = movieService.getMovieCertifications(type);
		return  new ResponseEntity<List<CertificationDTO>>(certifications.isPresent() ? certifications.get() : null, HttpStatus.OK);
	}
	
	@Operation(summary = "영화 제공업자 목록 조회", description = "영화 제공업자 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/providers")
	public ResponseEntity<List<ProviderDTO>> getProviderList(
			@RequestParam(name = "asynType") AsyncType type) {
		var providers = movieService.getProviders(type);
		return new ResponseEntity<List<ProviderDTO>>(providers.isPresent() ? providers.get() : null, HttpStatus.OK);
	}
	
	@Operation(summary = "영화 평가 등록", description = "사용자 ID 기준으로 영화 평가를 등록합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@Parameter(name = "sessionId", required = true, description = "세션 ID")
	@PostMapping("{movieId}/rating")
	public ResponseEntity<String> addRating(
			@PathVariable(name="movieId") String movieId
			, @RequestParam(name = "sessionId") String sessionId
			, @org.springframework.web.bind.annotation.RequestBody RatingRequest request ) {
		// TODO
		
		movieService.addRatingByUserId(RatingRequest.builder().movieId(movieId).sessionId(sessionId).value(request.getValue()).build());
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "영화 평가 삭제", description = "사용자 ID 기준으로 영화 평가를 삭제합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@DeleteMapping("{movieId}/rating")
	public ResponseEntity<String> deleteRating(@PathVariable(name = "movieId") String movieId
			) {
		// TODO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	// 추후 아래와 합 칠 수 있음.
	@Operation(summary = "영화 간단 검색", description = "영화 제목 기준으로 영화를 검색합니다.")
	@GetMapping("/search")
	public ResponseEntity<String> getSearchList(@RequestParam(name = "title") String title) {
		// TODO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "영화 필터 검색", description = "다양한 필터 기준으로 영화를 검색합니다.")
	@GetMapping("/search/multi")
	public ResponseEntity<String> getSearchListTypeFilter() {
		// TODO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}

}
