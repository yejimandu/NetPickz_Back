package com.netpickz.api.movie;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.netpickz.common.enumType.SortType;
import com.netpickz.common.enumType.TimeType;
import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.movie.service.MovieService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

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
		return  new ResponseEntity<MovieDTO>(movieDTO.isPresent() ? movieDTO.get() : null , HttpStatus.OK);
	}
	
	@Operation(summary = "영화 정보 조회", description = "영화 아이디 기준으로 영화 정보를 조회합니다.")
    @Parameter(name = "movieId", required = true, description = "영화 ID")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/{movieId}")
	public ResponseEntity<MovieDTO> getMovieInfo(
			@PathVariable(name = "movieId") String movieId, 
			@RequestParam(name = "asynType") AsyncType type) {
		var movieDTO =  movieService.getMovieInfoByMovieIdAndType(movieId, type);
		return  new ResponseEntity<MovieDTO>(movieDTO.isPresent() ? movieDTO.get() : null , HttpStatus.OK);
	}
	
	@Operation(summary = "타입별 영화 목록 조회", description = "원하는 영화 목록을 조회합니다.")
	@Parameter(name = "category" , description = "영화 카테고리")
	@GetMapping("/category")
	public ResponseEntity<List<MovieDTO>> getMovieList(   
		    @RequestParam(name = "category") MovieCategory category) {
		var movieDTOs =  movieService.getMovieListByType(category);
		return  new ResponseEntity<List<MovieDTO>>(movieDTOs.isPresent()? movieDTOs.get() : null, HttpStatus.OK);
	}
	
	@Operation(summary = "일간, 주간 기준으로 인기 영화 목록 조회", description = "일간, 주간 기준으로 원하는 영화 목록을 조회합니다.")
	@Parameter(name = "type" , description = "시간기준")
	@GetMapping("/trending")
	public ResponseEntity<List<MovieDTO>> getMovieListByTime(   
		    @RequestParam(name = "type") TimeType timeType) {
		var movieDTOs =  movieService.getMovieListByTimeType(timeType);
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
	
	// TODO
	@Operation(summary = "영화 평가 등록", description = "사용자 ID 기준으로 영화 평가를 등록합니다.")
	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@PostMapping("{movieId}/rating")
	public ResponseEntity<RatingDTO> addRating(
			@PathVariable(name="movieId") String movieId,
			@org.springframework.web.bind.annotation.RequestBody RatingRequest request) {
		// TODO
		var rationDto =  movieService.addRatingByUserId(movieId, request);
		return  new ResponseEntity<RatingDTO>(rationDto.isPresent()? rationDto.get() : null, HttpStatus.OK);
	}
	
	// TODO
	@Operation(summary = "영화 평가 삭제", description = "사용자 ID 기준으로 영화 평가를 삭제합니다.")
//	@Parameter(name = "movieId", required = true, description = "영화 ID")
	@Parameter(name = "sessionId", required = true, description = "세션아이디")
	@DeleteMapping("{movieId}/rating")
	public ResponseEntity<String> deleteRating(
			@PathVariable(name = "movieId") String movieId,
			@RequestParam(name="sessionId") String sessionId) {
		// TODO
		movieService.deleteRatingByUserId(movieId, sessionId);
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
    @Operation(summary = "영화 간단 검색", description = "영화 제목 기준으로 영화를 검색합니다.")
    @Parameter(name = "title", required = true, description = "영화 제목")
    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> getSearchList(
    		@RequestParam(name = "title") String title) {
    	var movieDTO =  movieService.getMovieListBySearch(title);
		return  new ResponseEntity<List<MovieDTO>>(movieDTO.isPresent() ? movieDTO.get() : null , HttpStatus.OK);
    }
    
	@Operation(summary = "영화 필터 검색", description = "다양한 필터 기준으로 영화를 검색합니다.")
	@Parameter(name = "page", required = false, description = "페이지번호")
	@Parameter(name = "withGenres", description = "특정 장르 포함", example = "28,15")
	@Parameter(name = "withPeople", description = "특정 인물이 출연 및 참여 포함" ,example = "홍길동" )
	@Parameter(name = "sortBy", description = "정렬기준"  )
	@Parameter(name = "includeAdult", description = "성인영화 포함 여부"  , example = "false")
	@GetMapping("/search/multi")
	public ResponseEntity<List<MovieDTO>> getSearchListTypeFilter(
			@RequestParam(name="page") int pageNum,
			@RequestParam(name="withGenres" , required = false) String withGenres,
			@RequestParam(name="withPeople" , required = false) String withPeople,
			@RequestParam(name="sortBy") SortType sortType,
			@RequestParam(name="includeAdult") Boolean includeAdult
			) {
		var filterRequest = FilterRequest.builder().includeAdult(includeAdult).pageNum(pageNum).sortType(sortType);
		if(StringUtils.isNotBlank(withGenres))filterRequest.withGenres(withGenres); 
		if(StringUtils.isNotBlank(withPeople))filterRequest.withPeople(withPeople); 
		
		var movieDtos = movieService.getMovieListByFilter(filterRequest.build());
		return  new ResponseEntity<List<MovieDTO>>(movieDtos.isPresent()? movieDtos.get() : null, HttpStatus.OK);
	}

}
