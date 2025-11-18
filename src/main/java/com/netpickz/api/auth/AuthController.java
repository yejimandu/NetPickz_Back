package com.netpickz.api.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.auth.request.AcessTokenRequest;
import com.netpickz.api.auth.request.AuthRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.api.movie.MovieController;
import com.netpickz.common.config.AppConfig;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.handler.CustomException;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

//@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 관련 기능을 제공하는 컨트롤러")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "swagger에서 사용자 로그인 (테스트 용) ", description = "사용자 로그인 처리합니다.")
	@GetMapping("")
	public ResponseEntity<TokenDTO> getGuestToken()  {
		// TODO
		System.out.println("getGuestToken");
		var tokenDTO = authService.createGuestToken();
		return ResponseEntity.status(HttpStatus.CREATED).body(tokenDTO);
	}
	
	@Operation(summary = "swagger에서 사용자 로그인 (테스트 용) ", description = "사용자 로그인 처리합니다.")
	@PostMapping("/swagger-login")
	public ResponseEntity<TokenDTO> userLogin(
			@org.springframework.web.bind.annotation.RequestBody LoginRequest request)  {
		// TODO
		var tokenDto = authService.userLogin(request);
		return  new ResponseEntity<TokenDTO>(tokenDto, HttpStatus.OK);
	}
	
	@Operation(summary = "swagger에서 사용자 로그아웃 (테스트 용) ", description = "사용자 로그아웃 처리합니다.")
	@PostMapping("/swagger-logout")
	public ResponseEntity<String> userLogout()  {
		// TODO
//		var tokenDto = authService.userLogout(request);
		return  new ResponseEntity<String>("", HttpStatus.OK);
	}
	
	@Operation(summary = "토큰 검증 ", description = "토큰기준으로 검증을 한다.")
	@PostMapping("/verify")
	public ResponseEntity<Map> verifyToken(
			@org.springframework.web.bind.annotation.RequestBody AcessTokenRequest request)  {
		// TODO
		var isValid = authService.verifyToken(request);
		var dd = Map.of("isValid" , isValid, "msg" , isValid ? "토큰이 유효합니다." : "토큰이 유효하지 않습니다. 재발급하십시오.");
		return  new ResponseEntity<Map>(dd, HttpStatus.OK);
	}
	
	
	@Operation(summary = "리프레쉬 토큰 기준으로 액세스 토큰 재발급", description = "리프레쉬 토큰 기준으로 액세스 토큰 재발급합니다. ")
	@PostMapping("/refresh")
	public ResponseEntity<String> refrechToken(
			@org.springframework.web.bind.annotation.RequestBody AuthRequest request)  {
		// TODO
		authService.reissue(request.getAccessToken(), request.getRefrechToken());
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
}
