package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import com.netpickz.core.movie.entity.MovieEntity;

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
@Table(name="user_rating_info")
public class UserRatingInfoEntity {
	@Id
	@Column(name="user_id")
	private String userId;
	
	@OneToOne
    @JoinColumn(name = "movie_id ", nullable = true)
    private MovieEntity movieEntity;
	
	@Column
	private float rating ;
	
	@Column(name="created_at")
	private Timestamp createdAt;
	
	@Column(name="modified_at ")
	private Timestamp modifiedAt ;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "userId")
	private UserEntity userEntity;
}
