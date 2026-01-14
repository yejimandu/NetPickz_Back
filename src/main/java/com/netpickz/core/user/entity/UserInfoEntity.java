package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.netpickz.common.enumType.StateType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name="user_info")
public class UserInfoEntity {
	
	@Id
	@Column(name="user_id")
	private String userId;

	@Column(nullable = false)
	private String name;

	@Column(unique = true)
	private String email ;

	@Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified; // 기본값 false
	
	@Column(name="include_adult", nullable = false)
	private boolean includeAdult;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private StateType state;
	
	@Column(name = "user_password", nullable = false, length = 100)
	private String password ;
	
	@Column(name="created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name="updated_at", updatable = true)
	@CreationTimestamp
	private Timestamp updatedAt;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "user_id")
	private UserEntity userEntity;
	
	
}
