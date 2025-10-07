package com.netpickz.core.auth.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.common.enumType.TokenStatusType;
import com.netpickz.core.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name="access_token")
	private String accessToken;
	
	@Column(name="refresh_token")
	private String refreshToken;
	
	@Column(name="issued_at")
	private String issuedAt; // long
	
	@Column(name="expires_at")
	private String expiresAt; //long
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private TokenStatusType status;
	
	@Column(name="created_at")
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name="updated_at")
	@UpdateTimestamp
	private Timestamp updatedAt;
	
}
