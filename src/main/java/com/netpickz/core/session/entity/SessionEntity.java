package com.netpickz.core.session.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.netpickz.core.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@Entity
@Table(name="session_info")
@ToString(exclude = {"userEntity"})
public class SessionEntity {
	
	@Id
    private String id;
	
    @Column(nullable = false, name="session_id", unique = true)
	private String sessionId;
	
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;
	
	@Column(name="expires_at" , nullable = false )
	private Timestamp expiresAt;
	
	@Column(name="created_at", nullable = false)
	@CreationTimestamp
	private Timestamp createdAt;
	
}
