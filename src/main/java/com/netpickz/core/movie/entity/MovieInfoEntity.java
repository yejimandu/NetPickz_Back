	package com.netpickz.core.movie.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.common.entity.CertificationEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="movie_info")
public class MovieInfoEntity {
	
	@Id
	@Column(name="movie_id")
	private String movieId ;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "movie_id")
	private MovieEntity movieEntity;
	
	@Column(name="over_view", length = 500)
	private String overView ;
	
	@Column
	private Integer runtime;

	@Column(name="poster_path")
	private String posterPath  ;

	@Column(name="release_date")
	private String releaseDate  ;
	
	@Column
	private String status  ;
	
	@Column(name="created_at")
	@UpdateTimestamp
	private Timestamp createdAt;
	
	
}
