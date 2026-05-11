package com.netpickz.core.user.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.core.session.entity.SessionEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name="users")
@ToString(exclude = {"userInfoEntity", "ratings", "sessionEntity"})
public class UserEntity {
	
	@Id
	@Column(name="user_id")
	private String userId;

	@Column(name="created_at", nullable =  false)
	@UpdateTimestamp
	private Timestamp createdAt;
	
	@OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true
			,fetch = FetchType.LAZY)
    private UserInfoEntity userInfoEntity;
	
	@OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true
    		,fetch = FetchType.LAZY)
//    @Builder.Default //
    private List<UserRatingInfoEntity> ratings = new ArrayList();

	@OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL, orphanRemoval = true
			,fetch = FetchType.LAZY)
//    @Builder.Default //
	private List<SessionEntity> sessionEntity = new ArrayList();
	
	
}
