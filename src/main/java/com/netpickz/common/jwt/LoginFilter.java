package com.netpickz.common.jwt;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.dto.ApiResponse;
import com.netpickz.common.dto.LogDTO;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.core.auth.service.AuthService;
import com.netpickz.core.log.service.LogService;
import com.netpickz.core.user.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter{

	private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
	private final LogService logService;
	private final PasswordEncoder encoder;
	
	public LoginFilter(AuthenticationManager authenticationManager, AuthService authService, ObjectMapper objectMapper, UserService userService, 
			LogService logService, PasswordEncoder encoder) {
		super.setAuthenticationManager(authenticationManager); // 부모에 세팅
        setFilterProcessesUrl("/auth/login"); // 경로 지정     
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.logService = logService;
        this.encoder = encoder;
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
			request.setAttribute("userId", username);
			
			var user = userService.getUserInfoByUserId(username)
					.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND));
			
			var matches = encoder.matches(password, user.getPassword());
			if(!matches) {
				log.error("Fail to Login By Password. userId={}", username);
				throw new NetPickzException(ErrorCode.USER_PASSWORD_INVALID);
			}
			
			// 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
			var authToken = new UsernamePasswordAuthenticationToken(username, password);
			// ✅ 부모가 가진 AuthenticationManager 사용  / token에 담은 검증을 위한 AuthenticationManager로 전달
            return this.getAuthenticationManager().authenticate(authToken);
		}catch (IOException  e) {
			 throw new RuntimeException(e);
		}
		
	}

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
				.data(tokens.getAccessToken())
				.status(HttpStatus.OK.value())
				.build();
	    
	    response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    
	    var errorCode = ErrorCode.LOGIN_FAIL;
		var apiResponse = ApiResponse.builder()
	 				.success(false)
	 				.message(errorCode.getMsg())
	 				.code(errorCode.getCode())
	 				.timeStamp(LocalDateTime.now())
	 				.status(HttpServletResponse.SC_UNAUTHORIZED)
	 				.build();
		 try {
	    	response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	    }catch(JsonProcessingException e) {
	    	log.error("Fail to Json Write. apiResponse={}", apiResponse.toString());
	    	throw new NetPickzException(ErrorCode.JSON_WRITE_FAIL);
	    }
		 
		var userId = request.getAttribute("userId").toString();
		logService.save(LogDTO.builder()
					.serviceName(request.getClass().getName()) 
					.path(request.getRequestURI()) 
					.method(request.getMethod().toString()) 
					.ipAddress(request.getRemoteAddr()) 
					.stackTrace(ExceptionUtils.getStackTrace(failed))
					.userAgent(request.getHeader("User-Agent"))
					.statusCode(errorCode.getStatus().value())
					.message(errorCode.getMsg())
					.userId(userId)
					.build());
	}
	
}
