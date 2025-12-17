package com.netpickz.core.auth.service;

import java.time.Duration;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.enumType.TokenStatusType;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.common.jwt.JWTUtil;
import com.netpickz.common.util.IdGenerator;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.dto.VerifyDTO;
import com.netpickz.core.auth.entity.TokenIssuanceHistoryEntity;
import com.netpickz.core.auth.repository.TokenIssuanceHistoryRepository;
import com.netpickz.core.auth.repository.TokenRepositoryCustom;
import com.netpickz.core.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JWTUtil jwtUtil;
	private final TokenIssuanceHistoryRepository tokenIssuanceHistoryRepository;
	private final TokenRepositoryCustom tokenRepositoryCustom;
	private final PasswordEncoder encoder;
	private final UserService userService;

	@Override
	public TokenDTO userLogin(LoginRequest request) {
		log.debug("Login User. userId={}", request.getUserId());
		var userId = request.getUserId();
		var password = request.getPassword();
		// 패스워드 검증
		var user = userService.getUserInfoByUserId(userId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND));
		var matches = encoder.matches(password, user.getPassword());
		return matches ? createToken(userId) : null;
	}

	@Transactional
	@Override
	public TokenDTO createToken(String username) {
		log.debug("Create Token. userId={}", username);
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
		try {
				var userId = jwtUtil.getUsername(access);
				var accessHash = jwtUtil.hashToken(access);
				var accessIssuedAt = jwtUtil.getIssuedAt(access);
				var accessExpiresAt = jwtUtil.getExpiresAt(access);
				var refreshHash = jwtUtil.hashToken(refresh);
			    var refreshIssuedAt = jwtUtil.getIssuedAt(refresh);
			    var refreshExpiresAt = jwtUtil.getExpiresAt(refresh);
			    
			    tokenRepositoryCustom.updateStateByUserId(userId, TokenStatusType.Inactive);
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
		} catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		} catch (JwtException e) {
			throw new NetPickzException(ErrorCode.TOKEN_INVALID);
		} catch (Exception e) {
			log.error("Fail to Save TokenIssuanceHistory. msg={}", e.getMessage(), e );
			throw new NetPickzException(ErrorCode.SERVER_ERROR);
		}
	}

	@Override
	public VerifyDTO verifyToken(AccessTokenRequest request) {
		try {
			var isVerify = jwtUtil.isExpired(request.getAccessToken());
			return  new VerifyDTO(!isVerify);
		}catch (MalformedJwtException e) {
			throw new NetPickzException(ErrorCode.ACCESS_TOKEN_MALFORMED);
		}catch (UnsupportedJwtException e) {
			throw new NetPickzException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);
		}catch (IllegalArgumentException  e) {
			throw new NetPickzException(ErrorCode.TOKEN_MISSING);
		}catch (JwtException  e) {
			throw new NetPickzException(ErrorCode.ACCESS_TOKEN_INVALID);
		}
	}
	
	@Override
	public TokenDTO reissueTokens(String refreshToken) {
		
		if(refreshToken == null || refreshToken.isEmpty()) {
			throw new NetPickzException(ErrorCode.REFRESH_TOKEN_NULL);
		} 
		
		try {
			jwtUtil.isExpired(refreshToken);
		}catch (ExpiredJwtException e) {
			throw new NetPickzException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}catch (MalformedJwtException e) {
			throw new NetPickzException(ErrorCode.REFRESH_TOKEN_MALFORMED);
		}catch (UnsupportedJwtException e) {
			throw new NetPickzException(ErrorCode.REFRESH_TOKEN_UNSUPPORTED);
		}catch (JwtException  e) {
			throw new NetPickzException(ErrorCode.REFRESH_TOKEN_INVALID);
		}
		
		// 카테고리 체크
	    var category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
        	throw new NetPickzException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
		
        var userId = jwtUtil.getUsername(refreshToken);
        return createToken(userId);
	}

	@Override
	public String userLogout(AccessTokenRequest request) {
		var userId = jwtUtil.getUsername(request.getAccessToken());
		if("guest".equals(userId)) {
			throw new NetPickzException(ErrorCode.AUTH_GUEST_NOT_ALLOWED);
		}
		try {
			tokenRepositoryCustom.updateStateByUserId(userId, TokenStatusType.Inactive);
			return "success";
		} catch (Exception e) {
			log.error("Fail to User Logout. msg={}", e.getMessage(), e );
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		}
	}

}
