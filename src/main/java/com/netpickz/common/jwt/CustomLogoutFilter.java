package com.netpickz.common.jwt;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.common.constants.Constants;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.core.auth.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean{

	private final AuthService authService;
	private final ObjectMapper objectMapper;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
	     //path and method verify 
		// TODO 테스트 필요
		log.debug("CustomLogoutFilter 호출. path={}, method={}", request.getRequestURI(), request.getMethod());
		// 1. 패스 체크
		var requestMethod  = request.getMethod();
//		if(!request.getRequestURI().equals("^\\\\/logout$") || !requestMethod.equals("POST")) {
		if(!(request.getRequestURI().equals("/auth/logout") && requestMethod.equals("POST"))) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// 2. 토큰 추출
		var token = request.getHeader("Authorization");
		// 토큰 널 여부 체크
		if(token == null) {
			log.debug("no Token. next Filter.");
			filterChain.doFilter(request, response);
			return;
		}
		var tokens = token.split("Bearer ");
		var accessToken = tokens[1];
	
		// 3. 로그아웃 처리
		authService.userLogout(AccessTokenRequest.builder()
								.accessToken(accessToken)
								.build());

        //4. cookie 값 초기화
        Cookie cookie = new Cookie(Constants.REFRESH, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        
        response.setStatus(HttpServletResponse.SC_OK);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    
	    var apiResponse = ApiResponse.builder()
				.success(true)
				.timeStamp(LocalDateTime.now())
				.status(HttpStatus.OK.value())
				.build();
	    
	    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}
}
