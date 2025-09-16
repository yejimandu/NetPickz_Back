package com.netpickz.core.movie.entity;

import java.sql.Timestamp;
import java.util.List;

import com.netpickz.common.entity.CertificationEntity;

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
	
	@Column(name="user_name")
	private String userName ;

	@Column(name="over_view")
	private String overView ;

	@Column(name="poster_path")
	private String posterPath  ;

	@Column(name="release_date")
	private String releaseDate   ;
	
    @OneToOne
    @JoinColumn(name = "certification_id", nullable = true)
    private CertificationEntity certificationEntity;
	
	@Column(name="created_at")
	private Timestamp createdAt;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "movieId")
	private MovieEntity movieEntity;
	
}
