package com.netpickz.core.auth.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import com.netpickz.common.enumType.TokenStatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name="token_issuance_history")
public class TokenIssuanceHistoryEntity {
	
	@Id
	private String id;
	
	@Comment("사용자 아이디")
	@Column(name = "user_id", nullable = false)
	private String userId;
	
	@Column(name="access_token_hash", nullable = false)
	private String accessTokenHash;
	
	@Comment("액세스 토큰 발급일")
	@Column(name="access_issued_at", nullable = false)
	private String  accessIssuedAt; // long
	
	@Comment("액세스 토큰 만료일")
	@Column(name="access_expires_at", nullable = false)
	private String  accessExpiresAt; //long
	
	@Column(name="refresh_token_hash", nullable = false)
	private String refreshTokenHash;
	
	@Comment("리프레쉬 토큰 발급일")
	@Column(name="refresh_issued_at", nullable = false)
	private String refreshIssuedAt; // long
	
	@Comment("리프레쉬 토큰 만료일")
	@Column(name="refresh_expires_at", nullable = false)
	private String refreshExpiresAt; //long
	
	@Comment("토큰 상태")
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private TokenStatusType status;
	
	@Column(name="created_at", nullable = false)
	@CreationTimestamp
	private Timestamp createdAt;

}
