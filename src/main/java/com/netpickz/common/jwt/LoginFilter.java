package com.netpickz.common.jwt;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.core.auth.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter{

//	private  AuthenticationManager authenticationManager;
	private final AuthService authService;
    private final ObjectMapper objectMapper;
    
	 public LoginFilter(AuthenticationManager authenticationManager, AuthService authService, ObjectMapper objectMapper) {
		super.setAuthenticationManager(authenticationManager); // 부모에 세팅
        setFilterProcessesUrl("/auth/login"); // 경로 지정     
        this.authService = authService;
        this.objectMapper = objectMapper;
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
	    var tokens = authService.createToken(username);
	    response.setHeader("access", tokens.getAccessToken());
	    if(tokens.getCookie() != null) response.addHeader("Set-Cookie",  tokens.getCookie().toString());
	    
	    // ✅ JSON 응답 (Controller와 동일한 형식)
	    response.setStatus(HttpStatus.OK.value());
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    
	    var apiResponse = ApiResponse.builder()
				.success(true)
				.timeStamp(LocalDateTime.now())
				.data(tokens)
				.status(HttpStatus.OK.value())
				.build();
	    
	    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}

	// TODO 로그인 실패 시 실행 되는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(401);
	}
	
}
