package com.netpickz.common.jwt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HexFormat;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;


@Component
public class JWTUtil {
	// 토큰 Payload에 저장될 정보 ( username, 생성일, 만료일)
	
	private SecretKey secretKey;
	
	public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
	}
	
	public String getUsername(String token) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
		}catch (ExpiredJwtException e) {
			return e.getClaims().get("username", String.class);
		}
	}
	
	public String getCategory(String token) {
		try {
			return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
		} catch (ExpiredJwtException  e) {
			return e.getClaims().get("category", String.class);
		}
	}
	
	public Boolean isExpired(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
	}
	
	public Long getExpiresAt(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().getTime() / 1000;
	}
	
	public Long getIssuedAt(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getIssuedAt().getTime() / 1000;
	}
	
	public String createJwt(String category, String username, Long expiredMs) {
		return Jwts.builder()
				.claim("category", category)
				.claim("username", username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiredMs ))
				.signWith(secretKey)
				.compact();
	}
	
	public String hashToken(String token) {
		
		try {
			// SHA-256 해시 객체 생성
			var digest = MessageDigest.getInstance("SHA-256");
			// 입력 문자열을 바이트 배열로 변환 후 해시 계산
			var hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
			
			// 바이트 배열을 HEX 문자열로 변환
			var hex = HexFormat.of().formatHex(hashBytes);
			System.out.println(hex);
			return hex;
		}catch (NoSuchAlgorithmException e) {
			throw new  RuntimeException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
		}
		
	}
}
