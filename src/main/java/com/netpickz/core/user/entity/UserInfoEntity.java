package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.common.enumType.SessionType;
import com.netpickz.common.enumType.StateType;
import com.netpickz.core.session.entity.SessionEntity;

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

	@Column(nullable = true)
	private String name;

	@Column(unique = true)
	private String email ;

	@Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified = false; // 기본값 false
	
	@Column(name="include_adult", nullable = false)
	private boolean includeAdult = false ;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private StateType state;
	
	@Column(name = "user_password", nullable = false, length = 100)
	private String password ;
	
	@Column(name="created_at")
	@CreationTimestamp
	private Timestamp createdAt;

	@Column(name="modified_at")
	@CreationTimestamp
	private Timestamp modifiedAt;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "user_id")
	private UserEntity userEntity;
	
	
}
