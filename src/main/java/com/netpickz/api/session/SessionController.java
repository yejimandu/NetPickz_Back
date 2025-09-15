package com.netpickz.api.session;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.netpickz.common.enumType.SessionType;
import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbGenreListResponse;
import com.netpickz.core.session.SessionDTO;
import com.netpickz.core.session.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/session")
@Tag(name = "Session", description = "세션 관련 기능을 제공하는 컨트롤러")
public class SessionController {

	@Autowired
	private TmdbClient tmdbClient;
	
	@Autowired
	private SessionService sessionService;
	
	@Operation(summary = "추천 API", description = "간단한 OK 응답 테스트")
	@GetMapping("/")
	public ResponseEntity<String> session() {
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "추천 API", description = "간단한 OK 응답 테스트")
	@GetMapping("/sdsd")
	public TmdbGenreListResponse session2() throws JsonMappingException, JsonProcessingException {
		ResponseEntity<TmdbGenreListResponse> ddd = tmdbClient.getGenreList();
		System.out.println(ddd);
		System.out.println(ddd.getBody());
//		return  new ResponseEntity<>("OK", HttpStatus.OK);
		return  ddd.getBody();
	}
	
	@Operation(summary = "추천 API", description = "간단한 OK 응답 테스트")
	@GetMapping("/guestSession")
	public TmdbGenreListResponse guestSession() throws JsonMappingException, JsonProcessingException {
//		ResponseEntity<TmdbGenreListResponse> ddd = tmdbClient.createGuestSession();
//		System.out.println(ddd);
//		System.out.println(ddd.getBody());
////		return  new ResponseEntity<>("OK", HttpStatus.OK);
		SessionDTO session = SessionDTO.builder()  // 자동완성 안되면 직접 타이핑
	            .sessionType(SessionType.User)
	            .requestToken("test")
	            .sessionId("test-session")
	            .expireDate(new Timestamp(System.currentTimeMillis()))
	            .build();
		sessionService.createSession(dto);
		return  ddd.getBody();
	}
	
	
}
