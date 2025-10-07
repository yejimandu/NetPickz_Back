package com.netpickz.common.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netpickz.core.auth.AuthService;
import com.netpickz.core.user.CustomUserDetails;
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
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		var accessToken = request.getHeader("access");
		// 토큰 널 여부 체크
		if(accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}
		String refresh = null;
	    var cookies = request.getCookies();
	    for (Cookie cookie : cookies) {
	        if (cookie.getName().equals("refresh")) {
	            refresh = cookie.getValue();
	        }
	    }
	
		// 토큰 만료 여부 확인
		try {
			jwtUtil.isExpired(accessToken);
		}catch (ExpiredJwtException e) {
			try {
				jwtUtil.isExpired(refresh);
				var dd = authService.reissue(accessToken, refresh);
				
				response.setHeader("access", dd.getAccessToken());
				response.addCookie(createCookie("refresh", dd.getRefreshToken()));
				response.setStatus(HttpStatus.OK.value());
				return;
			}catch (ExpiredJwtException  f) {
				// TODO 토큰 재발급
//				// response body 
				var writer = response.getWriter();
				writer.print("access token expired");
//				// response status
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}
		
		// 토큰이 access 인지 체크
		var category = jwtUtil.getCategory(accessToken);
		if(category != "access") {
			// response body 
			var writer = response.getWriter();
			writer.print("invalid access token");
			// response status
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		//
		var username = jwtUtil.getUsername(accessToken);
		// userInfoEntity 생성해서 값 set
		var userInfoEntiy = UserInfoEntity.builder().userId(username).build();
		// userDetail에다가 정보 넣기
		var customUserDetails = new CustomUserDetails(userInfoEntiy);
		//스프링 시큐리티 인증 토큰 생성
		var authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);
		filterChain.doFilter(request, response);
	}
	
	private Cookie createCookie(String key, String value) {
		var cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60);
//		cookie.setSecure(true); https 통신을 하는 경우 활성화
//		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		
//		String path = request.getRequestURI();
//		// 화이트리스트 경로는 그냥 통과
//		if (path.startsWith("/users") || path.equals("/login") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
//			filterChain.doFilter(request, response);
//			return;
//		}
//		
//		//request에서 Authorization 헤더를 찾음
//		var authorization= request.getHeader("Authorization");
//		//authorization 검증
//		if(authorization == null || !authorization.startsWith("Bearer ")) {
//			
//			System.out.println("token null");
//			filterChain.doFilter(request, response);
//			// 메소드 종료
//			return;
//		}
//		System.out.println("authorization now");
//		//Bearer 부분 제거 후 순수 토큰만 획득
//		var token = authorization.split(" ")[1];
//		// 토큰 시간 검증
//		if(jwtUtil.isExpired(token)) {
//			System.out.println("token expired");
//			filterChain.doFilter(request, response);
//		}
//		
//		// 토큰에서 username 획득
//		var username = jwtUtil.getUsername(token);
//		// userInfoEntity 생성해서 값 set
//		var userInfoEntiy = UserInfoEntity.builder().userId(username).password("tempPassword").build();
//		
//		// userDetail에다가 정보 넣기
//		var customUserDetails = new CustomUserDetails(userInfoEntiy);
//		
//		//스프링 시큐리티 인증 토큰 생성
//		var authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
//		//세션에 사용자 등록
//		SecurityContextHolder.getContext().setAuthentication(authToken);
//		
//		filterChain.doFilter(request, response);
//	}

}
