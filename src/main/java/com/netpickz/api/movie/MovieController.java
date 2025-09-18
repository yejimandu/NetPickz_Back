package com.netpickz.api.movie;

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

	@Operation(summary = "영화 정보 조회", description = "영화 아이디 기준으로 영화 정보를 조회합니다.")
    @Parameter(name = "id", required = true, description = "영화 ID")
	@ApiResponses(value = {
			@ApiResponse(description = "에러 설명", responseCode = "500" ),
			@ApiResponse( responseCode = "200",
				    description = "따봉"
)
	})
	@GetMapping("/{id}")
	public ResponseEntity<MovieDTO> getMovieInfo(
			@PathVariable(name = "id") String movieId) {
		movieService.findByMovieId(movieId);
		MovieDTO movieDTO = MovieDTO.builder().build();
		return  new ResponseEntity<>(movieDTO , HttpStatus.OK);
	}
	
	@Operation(summary = "타입별 영화 목록 조회", description = "원하는 영화 목록을 조회합니다.")
	@Parameter(name = "category" , description = "영화 카테고리")
	@GetMapping("")
	public ResponseEntity<String> getMovieList(   
		    @RequestParam(name = "category") MovieCategory category) {
		// TODO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "영화 제공업자 목록 조회", description = "원하는 영화를 시청할 수 있는 OTT 목록을 조회합니다.")
	@Parameter(name = "id", required = true, description = "영화 ID")
	@GetMapping("{id}/providers")
	public ResponseEntity<String> getMovieProviderList(   
			@PathVariable(name = "id") String movieId) {
		//TODO 
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	@Operation(summary = "비슷한 영화 목록 조회", description = "영화 ID 기준으로 비슷한 영화 목록을 조회합니다.")
	@Parameter(name = "id", required = true, description = "영화 ID")
	@GetMapping("/{id}/similar")
	public ResponseEntity<String> getMovieSimilarListByMovieId(   
			@PathVariable(name = "id") String movieId) {
		// TODO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "영화 장르 목록 조회", description = "영화 장르 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/genres")
	public ResponseEntity<String> getGenreList(
			@RequestParam(name = "asynType") AsyncType type) {
		// TODO
		System.out.println(type.getValue());
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "영화 관람 등급 목록 조회", description = "영화 관람 등급 목록을 조회합니다.")
	@Parameter(name = "asynType" , description = "데이터 갱신 타입")
	@GetMapping("/certifications")
	public ResponseEntity<String> getCertificationList(
			@RequestParam(name = "asynType") AsyncType type) {
		// TODO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "영화 평가 등록", description = "사용자 ID 기준으로 영화 평가를 등록합니다.")
	@Parameter(name = "id", required = true, description = "영화 ID")
	@PostMapping("{id}/rating")
	public ResponseEntity<String> addRating(
			@PathVariable(name="id") String movieId) {
		// TODO
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "영화 평가 삭제", description = "사용자 ID 기준으로 영화 평가를 삭제합니다.")
	 @Parameter(name = "id", required = true, description = "영화 ID")
	@DeleteMapping("{id}/rating")
	public ResponseEntity<String> deleteRating(@PathVariable(name = "id") String movieId) {
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
