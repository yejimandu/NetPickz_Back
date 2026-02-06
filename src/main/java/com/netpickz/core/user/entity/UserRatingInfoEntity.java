package com.netpickz.core.user.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.user.entity.pk.UserRatingInfoPK;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name="user_rating_info")
@ToString(exclude = {"movieEntity", "userEntity"})
public class UserRatingInfoEntity {
	
	@EmbeddedId
	private UserRatingInfoPK id;
	
//	@OneToOne
	@ManyToOne
	@MapsId("movieId")
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movieEntity;
	
//	@OneToOne
	@ManyToOne
	@MapsId("userId")
	@JoinColumn(name = "user_id" , nullable = false)
	private UserEntity userEntity;
	
	@Column(name="session_id", nullable = false)
	private String sessionId;
	
	@Column(nullable = false)
	private float rating ;
	
	@Column(name="created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Timestamp createdAt;
	
	@Column(name="updated_at", updatable = true)
	@UpdateTimestamp
	private Timestamp updatedAt ;
	
}
