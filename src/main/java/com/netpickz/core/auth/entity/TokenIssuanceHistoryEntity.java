package com.netpickz.core.auth.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
	@Column(name = "user_id")
	private String userId;
	
	@Column(name="access_token_hash")
	private String accessTokenHash;
	
	@Comment("액세스 토큰 발급일")
	@Column(name="access_issued_at")
	private String  accessIssuedAt; // long
	
	@Comment("액세스 토큰 만료일")
	@Column(name="access_expires_at")
	private String  accessExpiresAt; //long
	
	@Column(name="refresh_token_hash")
	private String refreshTokenHash;
	
	@Comment("리프레쉬 토큰 발급일")
	@Column(name="refresh_issued_at")
	private String refreshIssuedAt; // long
	
	@Comment("리프레쉬 토큰 만료일")
	@Column(name="refresh_expires_at")
	private String refreshExpiresAt; //long
	
	@Comment("토큰 상태")
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private TokenStatusType status;
	
	@Column(name="created_at", insertable = true, updatable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name="updated_at", insertable = true, updatable = true)
	@UpdateTimestamp
	private Timestamp updatedAt;
	
}
