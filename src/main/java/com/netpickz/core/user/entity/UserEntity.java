package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="users")
public class UserEntity {
	
	@Id
	@Column(name="user_id")
	private String userId;

	@Column(name="created_at", nullable =  false)
	@UpdateTimestamp
	private Timestamp createdAt;
}
