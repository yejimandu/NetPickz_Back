package com.netpickz.api.movie;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/movie")
@Tag(name = "Movie", description = "영화 관련 기능을 제공하는 컨트롤러")
public class MovieController {

//	private WebClient
//	
//	@Operation(summary = "영화 테스트 API", description = "간단한 OK 응답 테스트")
//	@GetMapping("/test")
//	public ResponseEntity<String> movieTest() {
//		
//		return  new ResponseEntity<>("OK", HttpStatus.OK);
//	}
//	
	@Operation(summary = "영화 API", description = "간단한 OK 응답 테스트")
	@GetMapping("/")
	public ResponseEntity<String> movie() {
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
}
