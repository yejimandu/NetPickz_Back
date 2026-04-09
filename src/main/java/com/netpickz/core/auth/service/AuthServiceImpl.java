package com.netpickz.core.auth.service;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.constants.Constants;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.enumType.TokenStatusType;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.common.jwt.JWTUtil;
import com.netpickz.common.util.IdGenerator;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.dto.VerifyDTO;
import com.netpickz.core.auth.entity.TokenIssuanceHistoryEntity;
import com.netpickz.core.auth.repository.TokenIssuanceHistoryRepository;
import com.netpickz.core.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JWTUtil jwtUtil;
	private final TokenIssuanceHistoryRepository tokenIssuanceHistoryRepository;
	private final PasswordEncoder encoder;
	private final UserService userService;

	@Override
	public TokenDTO userLogin(LoginRequest loginRequest, HttpServletRequest request) {
		log.warn("Login User. userId={}", loginRequest.getUserId());
		var userId = loginRequest.getUserId();
		var password = loginRequest.getPassword();
		request.setAttribute("userId", userId);
		// 패스워드 검증
		var user = userService.getUserInfoByUserId(userId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND));
		var matches = encoder.matches(password, user.getPassword());
		if(!matches) {
			log.error("Fail to Login By Password. userId={}", userId);
			throw new NetPickzException(ErrorCode.USER_PASSWORD_INVALID);
		}
		return createToken(userId);
	}

	@Override
	public TokenDTO createToken(String username) {
		log.debug("Create Token. userId={}", username);
		var access = jwtUtil.createJwt(Constants.ACCESS, username,  600000L); // 10분
		var refresh = jwtUtil.createJwt(Constants.REFRESH, username, 604800000L); // 7일
	    
	    if(!Constants.GUEST_TYPE.equals(username)) {
	    	createTokenByUserId(access, refresh);
	    }

	    var cookie = ResponseCookie
	    		.from("refresh", refresh)
	    		.httpOnly(true)
	    		.secure(true)
	    		.path("/")
//	            .sameSite("None") // CORS 환경에서 중요! 백엔드와 프론트 도메인 주소가 다를 때
	            .sameSite("Strict")  // TODO 나중에 서버 올릴 떄 이거로 백엔드와 프론트 도메인 주소가 동일 할 때.
	    		.maxAge(Duration.ofDays(7))
	    		.build();
	    
		return TokenDTO.builder()
				.accessToken(access)
				.cookie(cookie)
			    .build();
	}

	private void createTokenByUserId(String access, String refresh) {
		var userId = jwtUtil.getUsername(access);
		var accessHash = jwtUtil.hashToken(access);
		var accessIssuedAt = jwtUtil.getIssuedAt(access);
		var accessExpiresAt = jwtUtil.getExpiresAt(access);
		var refreshHash = jwtUtil.hashToken(refresh);
	    var refreshIssuedAt = jwtUtil.getIssuedAt(refresh);
	    var refreshExpiresAt = jwtUtil.getExpiresAt(refresh);
	    
	    tokenIssuanceHistoryRepository.updateStateByUserId(userId, TokenStatusType.Inactive);
	    tokenIssuanceHistoryRepository.save(TokenIssuanceHistoryEntity.builder()
    		.id(IdGenerator.getId("TN_"))
    		.userId(userId)
    		.accessTokenHash(accessHash)
    		.accessExpiresAt(accessExpiresAt.toString())
    		.accessIssuedAt(accessIssuedAt.toString())
    		.refreshTokenHash(refreshHash)
    		.refreshIssuedAt(refreshIssuedAt.toString())
    		.refreshExpiresAt(refreshExpiresAt.toString())
    		.status(TokenStatusType.Active)
    		.build());
	}

	@Override
	public VerifyDTO verifyToken(AccessTokenRequest tokenRequest, HttpServletRequest request) {
		try {
		    var token = tokenRequest.getAccessToken();
	        if (token == null || token.isBlank()) {
	            throw new NetPickzException(ErrorCode.TOKEN_MISSING);
	        }
	        
	        var username = jwtUtil.getUsername(token);
			request.setAttribute("userId", username);
			
			var expired = jwtUtil.isExpired(tokenRequest.getAccessToken());
			return new VerifyDTO(!expired ); // expired=false → valid=true
		}catch (IllegalArgumentException  e) {
			throw new NetPickzException(ErrorCode.TOKEN_MISSING);
		}
	}
	
	@Override
	public TokenDTO reissueTokens(String refreshToken, HttpServletRequest request) {
		request.setAttribute("userId", "UNKNOWN");
		
		if(refreshToken == null || refreshToken.isEmpty()) {
			throw new NetPickzException(ErrorCode.REFRESH_TOKEN_NULL);
		}
		
		try {
		    jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
		    throw new NetPickzException(ErrorCode.REFRESH_TOKEN_EXPIRED); // 재로그인 유도
		}
		
		var userId = jwtUtil.getUsername(refreshToken);
		request.setAttribute("userId", userId);
        if(userId == null || userId.isBlank()){
        	throw new NetPickzException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
	        
		// 카테고리 체크
	    var category = jwtUtil.getCategory(refreshToken);
    	if (!Constants.REFRESH.equals(category)) {
        	throw new NetPickzException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
       
        return createToken(userId);
	}

	@Override
	public String userLogout(AccessTokenRequest tokenRequest, HttpServletRequest request) {
		var msg = Constants.LOGOUT_SUCCESS;
		var userId = jwtUtil.getUsername(tokenRequest.getAccessToken());
		request.setAttribute("userId", userId);
		if(Constants.GUEST_TYPE.equals(userId)) {
			msg = Constants.LOGOUT_FAIL;
			throw new NetPickzException(ErrorCode.AUTH_GUEST_NOT_ALLOWED);
		}
		tokenIssuanceHistoryRepository.updateStateByUserId(userId, TokenStatusType.Inactive);
		return msg;
	}

}
