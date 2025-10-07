package com.netpickz.common.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.core.auth.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter{

//	private  AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final AuthService authService;
	
	 public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, AuthService authService) {
		super.setAuthenticationManager(authenticationManager); // 부모에 세팅
//        setFilterProcessesUrl("/swagger-login"); // 경로 지정
        setFilterProcessesUrl("/auth/login"); // 경로 지정     
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }
	 
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			// JSON 파싱
			var mapper = new ObjectMapper();
			var loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);
			
			// 클라이언트 요청에서 username, password 추출
			var username = loginRequest.getUserId();
			var password = loginRequest.getPassword();
			// 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
			var authToken = new UsernamePasswordAuthenticationToken(username, password);
			// ✅ 부모가 가진 AuthenticationManager 사용
            return this.getAuthenticationManager().authenticate(authToken);

			// token에 담은 검증을 위한 AuthenticationManager로 전달
//			return authenticationManager.authenticate(authToken);
		}catch (IOException  e) {
			 throw new RuntimeException(e);
		}
		
		
	}

	// TODO 로그인 성공 시 실행 되는 메소드 ( 단일 토큰 발급)
//	@Override
//	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
//			Authentication authentication) throws IOException, ServletException {
//		var customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//		var username = customUserDetails.getUsername();
//		var token = jwtUtil.createJwt(username, 60*60*10L);
//		response.addHeader("Authorization",  "Bearer " + token);
//	}
	
	// TODO 로그인 성공 시 다중 토큰 발급
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		var username = authentication.getName();

		// 토큰 생성
//		var access = jwtUtil.createJwt("access", username,  600000L); // 10분
//	    var refresh = jwtUtil.createJwt("refresh", username, 86400000L); // 24시간	
		
	    // TODO 토큰 생성 및 정보 DB에 저장하기.
	    var tokens = authService.createToken(username);
	    
	    response.setHeader("access", tokens.getAccessToken());
	    response.addCookie(createCookie("refresh", tokens.getRefreshToken()));
	    response.setStatus(HttpStatus.OK.value());
	}
	// TODO 합치지
	private Cookie createCookie(String key, String value) {
		var cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60);
//		cookie.setSecure(true); https 통신을 하는 경우 활성화
//		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	// TODO 로그인 실패 시 실행 되는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(401);
	}

	
}
