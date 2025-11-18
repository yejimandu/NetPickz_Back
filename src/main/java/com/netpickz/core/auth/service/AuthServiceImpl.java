package com.netpickz.core.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.auth.request.AcessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.common.enumType.TokenStatusType;
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
		var username = request.getUserId();
		var password = request.getPassword();
		// 패스워드 검증
		var user = userService.getUserInfoByUserId(username).get();
		var matches = encoder.matches(password, user.getPassword());
		return matches ? createToken(username) : null;
	}

	@Transactional
	@Override
	public TokenDTO createToken(String username) {
		var access = jwtUtil.createJwt("access", username,  600000L); // 10분
	    var refresh = jwtUtil.createJwt("refresh", username, 86400000L); // 24시간	
	    
	    var accessExpiresAt = jwtUtil.getExpiresAt(access);
	    var issuedAt = jwtUtil.getIssuedAt(refresh);
	    var refreshExpiresAt = jwtUtil.getExpiresAt(refresh);
	    var accessHash = jwtUtil.hashToken(access);
	    var refreshHash = jwtUtil.hashToken(refresh);
	    
	    
	    // 1. UserEntity 영속 참조 가져오기
	    var userToken = userTokensRepository.findById(username);
	    var userTokenEntity = new UserTokensEntity();
	    var user = entityManager.getReference(UserEntity.class, username);
	    if(!userToken.isPresent()) {
		    // TODO DB 저장
			userTokenEntity = UserTokensEntity.builder()
		    		.accessToken(accessHash)
		    		.refreshToken(refreshHash)
		    		.expiresAt(accessExpiresAt.toString())
		    		.refreshExpireAt(refreshExpiresAt.toString())
		    		.userEntity(user)
		    		.build();
	    }else {
	    	  // ✅ 이미 존재 → 조회한 엔티티 수정
	        userTokenEntity = userToken.get();
	        userTokenEntity.setAccessToken(accessHash);
	        userTokenEntity.setRefreshToken(refreshHash);
	        userTokenEntity.setExpiresAt(accessExpiresAt.toString());
	        userTokenEntity.setRefreshExpireAt(refreshExpiresAt.toString());
	    }
	    userTokensRepository.save(userTokenEntity);
	    tokenIssuanceHistoryRepository.save(TokenIssuanceHistoryEntity.builder()
	    		.id(IdGenerator.getId("TN_"))
	    		.userId(username)
	    		.accessToken(accessHash)
	    		.refreshToken(refreshHash)
	    		.issuedAt(issuedAt.toString())
	    		.expiresAt(refreshExpiresAt.toString())
	    		.status(TokenStatusType.Active)
	    		.build()
	    		);
	    
		return TokenDTO.builder()
				.accessToken(access)
			    .refreshToken(refresh)
			    .build();
	}

	@Override
	public Boolean verifyToken(AcessTokenRequest request) {
		try {
			var isExpired = jwtUtil.isExpired(request.getAccessToken());
			// TODO 만료시 제거
			return isExpired ? false : true;
		}catch (ExpiredJwtException e) {
			/// TODO 
			throw new RuntimeException("토큰이 만료되었습니다.", e);

		}
	}
	
	@Override
	public TokenDTO reissue(String accessToken, String refrechToken) {
//		if(refrechToken == null) return "refresh token null";
		
		//expired check
		try {
			jwtUtil.isExpired(refrechToken);
		}catch (ExpiredJwtException e) {
//			return "refresh token expired";
		}
		
		// 카테고리 체크
	    var category = jwtUtil.getCategory(refrechToken);
        if (!category.equals("refresh")) {
            //response status code
//            return "invalid refresh token";
        }
		
        var username = jwtUtil.getUsername(refrechToken);
        // 새로운 jwt 생성
        var tokenDto =  createToken(username);
        
        // TODO DB에 갱신 리턴 타입 고민
        return tokenDto;
	}

	@Override
	public TokenDTO createGuestToken() {
		var access = jwtUtil.createJwt("access", "guest",  600000L); // 10분
	    var refresh = jwtUtil.createJwt("refresh", "guest", 3600000L); // 24시간	
	    
//	    var accessExpiresAt = jwtUtil.getExpiresAt(access);
//	    var issuedAt = jwtUtil.getIssuedAt(refresh);
//	    var refreshExpiresAt = jwtUtil.getExpiresAt(refresh);
//	    var accessHash = jwtUtil.hashToken(access);
//	    var refreshHash = jwtUtil.hashToken(refresh);

		return TokenDTO.builder()
				.accessToken(access)
			    .refreshToken(refresh)
			    .build();
	}
}
