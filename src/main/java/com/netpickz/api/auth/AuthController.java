package com.netpickz.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.AuthRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.constants.Constants;
import com.netpickz.core.auth.dto.VerifyDTO;
import com.netpickz.core.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
	public ResponseEntity<String> getGuestToken(HttpServletResponse response)  {
		var tokenDTO = authService.createToken(Constants.GUEST_TYPE);
		response.addHeader("Set-Cookie", tokenDTO.getCookie().toString());
		return ResponseEntity.ok(tokenDTO.getAccessToken());
	}
	
	@Operation(summary = "swagger에서 사용자 로그인 (테스트 용) ", description = "사용자 로그인 처리합니다.")
	@PostMapping("/swagger-login")
	public ResponseEntity<String> userLogin(
			@org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response)  {
		var tokenDTO = authService.userLogin(loginRequest, request);
		response.addHeader("Set-Cookie", tokenDTO.getCookie().toString());
		return ResponseEntity.ok(tokenDTO.getAccessToken());
	}
	
	@Operation(summary = "토큰 검증", description = "토큰 기준으로 검증을 한다.")
	@PostMapping("/verify")
	public ResponseEntity<VerifyDTO> verifyToken(
			@org.springframework.web.bind.annotation.RequestBody AccessTokenRequest tokenRequest, HttpServletRequest request)  {
		var verifyDTO = authService.verifyToken(tokenRequest, request);
		return ResponseEntity.ok(verifyDTO);
	}
	
	@Operation(summary = "리프레쉬 토큰 기준으로 액세스 토큰 재발급", description = "리프레쉬 토큰 기준으로 액세스 토큰 재발급합니다. ")
	@PostMapping("/refresh")
	public ResponseEntity<String> refrechToken(
			@org.springframework.web.bind.annotation.RequestBody AuthRequest authRequest , HttpServletRequest request, HttpServletResponse response)  {
		var tokenDTO = authService.reissueTokens(authRequest.getRefrechToken(), request);
		response.addHeader("Set-Cookie", tokenDTO.getCookie().toString());
		return ResponseEntity.ok(tokenDTO.getAccessToken());
	}
	
	@Operation(summary = "swagger에서 사용자 로그아웃 (테스트 용) ", description = "사용자 로그아웃 처리합니다.")
	@PostMapping("/swagger-logout")
	public ResponseEntity<String> userLogout(
			@org.springframework.web.bind.annotation.RequestBody AccessTokenRequest tokenRequest, HttpServletRequest request, HttpServletResponse response)  {
		var msg = authService.userLogout(tokenRequest, request);
		response.addHeader("Set-Cookie", "refresh=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=Strict");
		return ResponseEntity.ok(msg);
	}
	
}
