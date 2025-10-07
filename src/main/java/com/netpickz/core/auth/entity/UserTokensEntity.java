package com.netpickz.core.auth.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.netpickz.core.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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
@Table(name="user_tokens")
public class UserTokensEntity {

	@Id
	@Column(name="user_id")
	private String userId;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "user_id")
	private UserEntity userEntity;

	@Column(name="access_token")
	private String accessToken;
	
	@Column(name="refresh_token")
	private String refreshToken;
	
	@Column(name="expires_at")
	private String expiresAt; // long
	
	@Column(name="refresh_expire_at")
	private String refreshExpireAt; // long
	
	@Column(name="created_at")
	@CreationTimestamp
	private Timestamp createdAt;
	
}
