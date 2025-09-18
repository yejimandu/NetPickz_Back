package com.netpickz.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 관련 기능을 제공하는 컨트롤러")
public class AuthController {

	
	@Operation(summary = "인증 API", description = "간단한 OK 응답 테스트")
	@GetMapping("/auth")
	public ResponseEntity<String> auth() {   
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	
	@Operation(summary = "사용자 TMDB 토큰 발급 ", description = "TMDB 요청 토큰 발급 후 승인 창으로 리다이렉트 처리 ")
	@GetMapping("/request-token")
	public ResponseEntity<String> userToken()  {
		// TOTO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@Operation(summary = "사용자 로그인 처리 ", description = "TMDB 발급된 토큰으로 세션 생성 후 로그인 처리")
	@GetMapping("/session")
	public ResponseEntity<String> userLogin()  {
		// TOTO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
}
