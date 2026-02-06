package com.netpickz.core.auth.service;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.constants.Constants;
import com.netpickz.common.dto.CommonDTO;
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
	public TokenDTO userLogin(LoginRequest request) {
		log.warn("Login User. userId={}", request.getUserId());
		var userId = request.getUserId();
		var password = request.getPassword();
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
	    var refresh = jwtUtil.createJwt(Constants.REFRESH, username, 86400000L); // 24시간	
	    
	    if(!Constants.GUEST_TYPE.equals(username)) {
	    	createTokenByUserId(access, refresh);
	    }

	    var cookie = ResponseCookie
	    		.from("refresh", refresh)
	    		.httpOnly(true)
	    		.secure(true)
	    		.path("/")
	            .sameSite("None") // CORS 환경에서 중요!
//	            .sameSite("Strict")  
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
	public VerifyDTO verifyToken(AccessTokenRequest request) {
		try {
			var expired = jwtUtil.isExpired(request.getAccessToken());
			return new VerifyDTO(!expired ); // expired=false → valid=true
		}catch (IllegalArgumentException  e) {
			throw new NetPickzException(ErrorCode.TOKEN_MISSING);
		}
	}
	
	@Override
	public TokenDTO reissueTokens(String refreshToken) {
		
		if(refreshToken == null || refreshToken.isEmpty()) {
			throw new NetPickzException(ErrorCode.REFRESH_TOKEN_NULL);
		}
		
		jwtUtil.isExpired(refreshToken);
		
		// 카테고리 체크
	    var category = jwtUtil.getCategory(refreshToken);
    	if (!Constants.REFRESH.equals(category)) {
        	throw new NetPickzException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
		
        var userId = jwtUtil.getUsername(refreshToken);
        if(userId == null || userId.isBlank()){
        	throw new NetPickzException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        return createToken(userId);
	}

	@Override
	public CommonDTO userLogout(AccessTokenRequest request) {
		var userId = jwtUtil.getUsername(request.getAccessToken());
		if(Constants.GUEST_TYPE.equals(userId)) { 
			throw new NetPickzException(ErrorCode.AUTH_GUEST_NOT_ALLOWED);
		}
		tokenIssuanceHistoryRepository.updateStateByUserId(userId, TokenStatusType.Inactive);
		return CommonDTO.builder().status(true).message(Constants.LOGOUT_SUCCESS).build();
	}

}
