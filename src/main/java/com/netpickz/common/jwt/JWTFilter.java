package com.netpickz.common.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.service.AuthService;
import com.netpickz.core.user.entity.UserInfoEntity;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter{

	private final JWTUtil jwtUtil;
	private final AuthService authService;
	
	// ObjectMapper를 싱글톤으로 관리
	private ObjectMapper mapper;
	
	private ObjectMapper getObjectMapper() {
		if(mapper == null) {
			mapper = new ObjectMapper();
		    mapper.registerModule(new JavaTimeModule()); // 이거 추가!
		    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); 
		}
	    return mapper;
	}
	
	// 필터 제외할 경로들
    private static final List<String> EXCLUDE_URLS = List.of(
        "/auth/login",
        "/users"
//        "/movies/**"
    );
    
    private static final List<String> STARTWITH_EXCLUDE_URLS  = List.of(
		"/swagger-ui",
        "/v3/api-docs",
        "/auth");
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		var path = request.getRequestURI();
		var startsWithPath = STARTWITH_EXCLUDE_URLS.stream().anyMatch(path::startsWith);
		var equalsPath = EXCLUDE_URLS.stream().anyMatch(path::equals);
		
		return startsWithPath || equalsPath;
	}
	
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		var token = request.getHeader("Authorization");
		var tokenss = token.split("Bearer ");
		var accessToken = tokenss[1];
		// 토큰 널 여부 체크
		if(accessToken == null) {
			 System.out.println("토큰 없음, 다음 필터로");
			filterChain.doFilter(request, response);
			return;
		}
		
	    var cookies = request.getCookies();
	    var refresh =  Arrays.stream(cookies)
	            .filter(c -> "refresh".equals(c.getName()))
   	            .map(Cookie::getValue)
	            .findFirst()
	            .orElse(null); 
	
		// 토큰 만료 여부 확인
		try {
			jwtUtil.isExpired(accessToken);
		}catch (ExpiredJwtException e) {
			 System.out.println("Access 토큰 만료, 재발급 시도");
			 handleTokenReissue(refresh , response);
		}
		
		// 토큰이 access 인지 체크
		var category = jwtUtil.getCategory(accessToken);
		if(!"access".equals(category)) {
			// response body 
			var writer = response.getWriter();
			writer.print("invalid access token");
			// response status
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		var username = jwtUtil.getUsername(accessToken);
		if(!"guest".equals(username)) {
			// userInfoEntity 생성해서 값 set
			var userInfoEntiy = UserInfoEntity.builder().userId(username).build();
			// userDetail에다가 정보 넣기
			var customUserDetails = new CustomUserDetails(userInfoEntiy);
			//스프링 시큐리티 인증 토큰 생성
			var authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
			//세션에 사용자 등록
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}
		System.out.println("=== JWT Filter 통과 ===");
		filterChain.doFilter(request, response);
	}


	private void handleTokenReissue(String refresh, HttpServletResponse response) throws IOException {
		try {
			var tokens = authService.reissueTokens(refresh);
			// 성공 응답
			sendSuccessResponse(response, tokens);
		}catch (ExpiredJwtException  f) {
			// response body 
			var writer = response.getWriter();
			writer.print("access token expired");
			// response status
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
	}


	private void sendSuccessResponse(HttpServletResponse response, TokenDTO tokens) throws JsonProcessingException, IOException {
		response.setHeader("access", tokens.getAccessToken());
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
	   
	    response.getWriter().write(getObjectMapper().writeValueAsString(apiResponse));
	}

//	private void sendErrorResponse(HttpServletResponse response, HttpStatus status, ErrorCode errorCode) throws JsonProcessingException, IOException {
//		// ✅ JSON 응답 (Controller와 동일한 형식)
//	    response.setStatus(status.value());
//	    response.setContentType("application/json");
//	    response.setCharacterEncoding("UTF-8");
//	    
//	    var apiResponse = ApiResponse.builder()
//				.success(false)
//				.timeStamp(LocalDateTime.now())
//				.code(errorCode.getCode())
//				.message(errorCode.getMsg())
//				.status(status.value())
//				.build();
//	   
//	    response.getWriter().write(getObjectMapper().writeValueAsString(apiResponse));
//	}
//	
}
