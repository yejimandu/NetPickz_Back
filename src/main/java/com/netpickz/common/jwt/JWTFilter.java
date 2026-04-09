package com.netpickz.common.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.common.constants.Constants;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.handler.NetPickzException;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter{

	private final JWTUtil jwtUtil;
	private final AuthService authService;
	private final ObjectMapper mapper;
	
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
		log.info("path = {}", request.getRequestURI());
		var path = request.getRequestURI();
		var startsWithPath = STARTWITH_EXCLUDE_URLS.stream().anyMatch(path::startsWith);
		var equalsPath = EXCLUDE_URLS.stream().anyMatch(path::equals);
		
		return startsWithPath || equalsPath;
	}
	
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		var token = request.getHeader("Authorization");
		if(token == null) {
			log.debug("Token no, next filter. token={}", token);
			filterChain.doFilter(request, response);
			return;
		}
		var tokens = token.split("Bearer ");
		var accessToken = tokens[1];
		if(accessToken.length() < 2) {
			log.debug("accessToken no, next filter. token={}", token);
			filterChain.doFilter(request, response);
			sendErrorResponse(response, ErrorCode.TOKEN_MISSING);
			return;
		}
		
	    var cookies = request.getCookies();
	    var refresh = new String();
	    if(cookies != null) {
		     refresh =  Arrays.stream(cookies)
		            .filter(c -> Constants.REFRESH.equals(c.getName()))
	   	            .map(Cookie::getValue)
		            .findFirst()
		            .orElse(null); 
	    }
	    
	    try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			var category = jwtUtil.getCategory(accessToken);
			if(Constants.RESET_TOKEN.equals(category)) {
				log.debug(" access 토큰 만료. 재발급 링크 만료");
				log.debug("Expired Jwt. accessToken={}", accessToken);
				sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
			} else {
				log.debug(" access 토큰 만료. 재발급 시도");
				log.debug("Expired Jwt. Reissue to Token. accessToken={}", accessToken);
				handleTokenReissue(refresh , request,response); 
			}
			return;
		}
	    
	    var category = jwtUtil.getCategory(accessToken);
	    if(!Constants.ACCESS.equals(category) && !Constants.RESET_TOKEN.equals(category)) {
			// response body 
			var writer = response.getWriter();
			writer.print("invalid access token");
			// response status
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
	    
		var username = jwtUtil.getUsername(accessToken);
		request.setAttribute("userId", username);
		if(!Constants.GUEST_TYPE.equals(username)) {
			// userInfoEntity 생성해서 값 set
			var userInfoEntiy = UserInfoEntity.builder().userId(username).build();
			// userDetail에다가 정보 넣기
			var customUserDetails = new CustomUserDetails(userInfoEntiy);
			//스프링 시큐리티 인증 토큰 생성
			var authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
			//세션에 사용자 등록
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}
		log.debug("======JWT Filter 통과 =====");
		filterChain.doFilter(request, response);
	}

	private void handleTokenReissue(String refresh, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			var tokens = authService.reissueTokens(refresh, request);
			response.setHeader("Authorization", "Bearer " + tokens.getAccessToken());
			response.addHeader("Set-Cookie", tokens.getCookie().toString());
			sendErrorResponse(response,  ErrorCode.ACCESS_TOKEN_EXPIRED); 
			return;
		}catch (ExpiredJwtException  f) {
			// response body 
			var writer = response.getWriter();
			writer.print("access token expired");
			// response status
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}catch (NetPickzException e) {
	        sendErrorResponse(response, e.getErrorCode()); // ← 추가
	    }
	}

	private void sendSuccessResponse(HttpServletResponse response, TokenDTO tokens) throws JsonProcessingException, IOException {
		response.setHeader("Authorization", "Bearer " + tokens.getAccessToken());
		
//		response.setHeader("access", tokens.getAccessToken());
		// ✅ JSON 응답 (Controller와 동일한 형식)
	    response.setStatus(HttpStatus.OK.value());
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.addHeader("Set-Cookie", tokens.getCookie().toString());
	    
	    var apiResponse = ApiResponse.builder()
				.success(true)
				.timeStamp(LocalDateTime.now())
				.data(tokens.getAccessToken())
				.status(HttpStatus.OK.value())
				.build();
	   log.info("JWTFilter sendSuccessResponse apiResponse:{} " , apiResponse.toString());
	    response.getWriter().write(mapper.writeValueAsString(apiResponse));
	}

	// TODO
	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws JsonProcessingException, IOException {
	    response.setStatus(errorCode.getStatus().value());
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
//		log.error("NetPickzException 발생. errorCode={}, msg={}", errorCode, errorCode.getMsg(), e);
		var apiResponse = ApiResponse.builder()
 				.success(false)
 				.message(errorCode.getMsg())
 				.code(errorCode.getCode())
 				.timeStamp(LocalDateTime.now())
 				.status(errorCode.getStatus().value())
 				.build();
//		log.info("GlobalExceptionHandler handlerException apiResponse : " + apiResponse.toString() );
	    response.getWriter().write(mapper.writeValueAsString(apiResponse));
	}
	
}
