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
}
