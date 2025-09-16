package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import com.netpickz.core.session.SessionEntity;

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
@Table(name="user_info")
public class UserInfoEntity {
	
	@Id
	@Column(name="user_id")
	private String userId;

	@Column(nullable = true)
	private String name;

	@Column
	private String email ;

	@Column(name="include_adult", nullable = false)
	private boolean includeAdult = false ;
	
    @OneToOne
    @JoinColumn(name = "session_id ")
    private SessionEntity sessionEntity;
	
	@Column(name="created_at", nullable =  false)
	private Timestamp createdAt;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "userId")
	private UserEntity userEntity;
	
	
}
