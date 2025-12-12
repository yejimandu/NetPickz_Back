package com.netpickz.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.AuthRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.dto.VerifyDTO;
import com.netpickz.core.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173") // 
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 관련 기능을 제공하는 컨트롤러")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "게스트 토큰 발급", description = "로그인전 통신을 위한 게스트용 토큰 발급합니다.")
	@GetMapping("")
	public ResponseEntity<TokenDTO> getGuestToken(HttpServletResponse response)  {
		var tokenDTO = authService.createToken("guest");
		response.addHeader("Set-Cookie", tokenDTO.getCookie().toString()); // 
		return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
	}
	
	@Operation(summary = "swagger에서 사용자 로그인 (테스트 용) ", description = "사용자 로그인 처리합니다.")
	@PostMapping("/swagger-login")
	public ResponseEntity<TokenDTO> userLogin(
			@org.springframework.web.bind.annotation.RequestBody LoginRequest request)  {
		var tokenDTO = authService.userLogin(request);
		return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
	}
	
	@Operation(summary = "토큰 검증", description = "토큰 기준으로 검증을 한다.")
	@PostMapping("/verify")
	public ResponseEntity<VerifyDTO> verifyToken(
			@org.springframework.web.bind.annotation.RequestBody AccessTokenRequest request)  {
		var verifyDTO = authService.verifyToken(request);
		return ResponseEntity.status(HttpStatus.OK).body(verifyDTO);
	}
	
	@Operation(summary = "리프레쉬 토큰 기준으로 액세스 토큰 재발급", description = "리프레쉬 토큰 기준으로 액세스 토큰 재발급합니다. ")
	@PostMapping("/refresh")
	public ResponseEntity<TokenDTO> refrechToken(
			@org.springframework.web.bind.annotation.RequestBody AuthRequest request)  {
		var tokenDTO = authService.reissueTokens(request.getRefrechToken());
		return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
	}
	
	@Operation(summary = "swagger에서 사용자 로그아웃 (테스트 용) ", description = "사용자 로그아웃 처리합니다.")
	@PostMapping("/swagger-logout")
	public ResponseEntity<String> userLogout(
			@org.springframework.web.bind.annotation.RequestBody AccessTokenRequest request)  {
		// TODO 로그아웃 시 토큰 상태 값 변경 및 쿠키에서 삭제.
		var success = authService.userLogout(request);
		return ResponseEntity.status(HttpStatus.OK).body(success);
	}
	
}
