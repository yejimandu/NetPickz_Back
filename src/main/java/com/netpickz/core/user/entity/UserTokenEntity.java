package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name="user_token_info")
public class UserTokenEntity {
	@Id
	@Column(name="user_id")
	private String userId;
	
	@Column(nullable =  false, name="request_token")
	private String requestToken ;
	
	@Column(name="expires_at", nullable =  false)
	private Timestamp expiresAt;
	
	@Column(name="created_at", nullable =  false)
	private Timestamp createdAt;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "userId")
	private UserEntity userEntity;

}
