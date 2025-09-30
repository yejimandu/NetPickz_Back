package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.core.movie.entity.MovieEntity;

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
@Table(name="user_rating_info")
public class UserRatingInfoEntity {
	@Id
	@Column(name="user_id")
	private String userId;
	
	@OneToOne
    @JoinColumn(name = "movie_id ", nullable = true)
    private MovieEntity movieEntity;
	
	@Column(name="guest_session_id")
	private String guestSessionId;
	
	@Column
	private float rating ;
	
	@Column(name="created_at")
	@CreationTimestamp
	private Timestamp createdAt;
	
	@Column(name="modified_at ")
	@UpdateTimestamp
	private Timestamp modifiedAt ;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "userId")
	private UserEntity userEntity;
}
