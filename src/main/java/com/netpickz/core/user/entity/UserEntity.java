package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name="users")
public class UserEntity {
	
	@Id
	@Column(name="user_id")
	private String userId;
	
	@Column(nullable = true, name="account_id")
	private String accountId ;
	
	@Column(name="created_at", nullable =  false)
	private Timestamp createdAt;
}
