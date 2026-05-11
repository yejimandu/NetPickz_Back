	package com.netpickz.core.movie.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.common.entity.CertificationEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="movie_info")
@ToString(exclude = {"movieEntity"})
public class MovieInfoEntity {
	
	@Id
	@Column(name="movie_id")
	private String movieId ;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "movie_id")
	private MovieEntity movieEntity;
	
	@Column(length = 1000)
	private String overView ;
	
	@Column
	private Integer runtime;

	@Column(name="poster_path")
	private String posterPath  ;

	@Column(name="release_date")
	private String releaseDate  ;
	
	@Column(nullable = false)
	private String status  ;
	
	@Column(name="created_at", nullable = false, updatable = false) 
	@UpdateTimestamp
	private Timestamp createdAt;
	
	@Column(name="updated_at", updatable = true)
	@UpdateTimestamp
	private Timestamp updatedAt ;
	
	@ManyToOne
	@JoinColumn(name="certification_id")  //TODO 
	private CertificationEntity certificationEntity ;
}
