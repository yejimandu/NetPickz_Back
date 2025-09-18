package com.netpickz.core.session;

import java.sql.Timestamp;

import com.netpickz.common.enumType.SessionType;
import com.netpickz.core.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@Entity
@Table(name="session_info")
public class SessionEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(unique = true, nullable = false, name="session_id")
	private String sessionId;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
	private SessionType type;
	
    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity userEntity;
	
	@Column(name="expires_at" , nullable = true )
	private Timestamp expiresAt;
	
	@Column(name="created_at")
	private Timestamp createdAt;
	
}
