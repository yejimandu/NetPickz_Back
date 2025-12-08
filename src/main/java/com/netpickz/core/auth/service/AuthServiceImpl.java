package com.netpickz.core.auth.service;

import java.time.Duration;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.enumType.TokenStatusType;
import com.netpickz.common.handler.CustomException;
import com.netpickz.common.jwt.JWTUtil;
import com.netpickz.common.util.IdGenerator;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.entity.TokenIssuanceHistoryEntity;
import com.netpickz.core.auth.entity.UserTokensEntity;
import com.netpickz.core.auth.repository.TokenIssuanceHistoryRepository;
import com.netpickz.core.auth.repository.UserTokensRepository;
import com.netpickz.core.user.entity.UserEntity;
import com.netpickz.core.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JWTUtil jwtUtil;
	private final UserTokensRepository userTokensRepository;
	private final TokenIssuanceHistoryRepository tokenIssuanceHistoryRepository;
	private final EntityManager entityManager;
	private final PasswordEncoder encoder;
	private final UserService userService;

	@Override
	public TokenDTO userLogin(LoginRequest request) {
		var userId = request.getUserId();
		var password = request.getPassword();
		// 패스워드 검증
		var user = userService.getUserInfoByUserId(userId).get();
		var matches = encoder.matches(password, user.getPassword());
		return matches ? createToken(userId) : null;
	}

	@Transactional
	@Override
	public TokenDTO createToken(String username) {
		var access = jwtUtil.createJwt("access", username,  600000L); // 10분
	    var refresh = jwtUtil.createJwt("refresh", username, 86400000L); // 24시간	
	    
	    if(!"guest".equals(username)) {
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
		var accessExpiresAt = jwtUtil.getExpiresAt(access);
	    var issuedAt = jwtUtil.getIssuedAt(refresh);
	    var refreshExpiresAt = jwtUtil.getExpiresAt(refresh);
	    var accessHash = jwtUtil.hashToken(access);
	    var refreshHash = jwtUtil.hashToken(refresh);
	    
	    // 1. UserEntity 영속 참조 가져오기
	    var user = entityManager.getReference(UserEntity.class, userId);
	    var userToken = userTokensRepository.findById(userId);
	    var userTokenEntity = userToken.orElseGet(() ->
		    UserTokensEntity.builder()
			.accessToken(accessHash)
			.refreshToken(refreshHash)
			.expiresAt(accessExpiresAt.toString())
			.refreshExpireAt(refreshExpiresAt.toString())
			.userEntity(user)
			.build());

    	  // ✅ 이미 존재 → 조회한 엔티티 수정
        userTokenEntity.setAccessToken(accessHash);
        userTokenEntity.setRefreshToken(refreshHash);
        userTokenEntity.setExpiresAt(accessExpiresAt.toString());
        userTokenEntity.setRefreshExpireAt(refreshExpiresAt.toString());
	        
	    userTokensRepository.save(userTokenEntity);
	    
	    tokenIssuanceHistoryRepository.save(TokenIssuanceHistoryEntity.builder()
    		.id(IdGenerator.getId("TN_"))
    		.userId(userId)
    		.accessToken(accessHash)
    		.refreshToken(refreshHash)
    		.issuedAt(issuedAt.toString())
    		.expiresAt(refreshExpiresAt.toString())
    		.status(TokenStatusType.Active)
    		.build()
		);
	}

	@Override
	public Boolean verifyToken(AccessTokenRequest request) {
		try {
			return jwtUtil.isExpired(request.getAccessToken());
		}catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		}catch (MalformedJwtException e) {
			throw new CustomException(ErrorCode.ACCESS_TOKEN_MALFORMED);
		}catch (UnsupportedJwtException e) {
			throw new CustomException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);
		}catch (IllegalArgumentException  e) {
			throw new CustomException(ErrorCode.TOKEN_MISSING);
		}catch (JwtException  e) {
			throw new CustomException(ErrorCode.ACCESS_TOKEN_INVALID);
		}
	}
	
	@Override
	public TokenDTO reissueTokens(String refreshToken) {
		
		if(refreshToken == null || refreshToken.isEmpty()) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_NULL);
		} 
		
		try {
			jwtUtil.isExpired(refreshToken);
		}catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}catch (MalformedJwtException e) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_MALFORMED);
		}catch (UnsupportedJwtException e) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_UNSUPPORTED);
		}catch (JwtException  e) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
		}
		
		// 카테고리 체크
	    var category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
        	throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
		
        var userId = jwtUtil.getUsername(refreshToken);
        return createToken(userId);
	}

}
