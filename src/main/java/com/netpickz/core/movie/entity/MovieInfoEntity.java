package com.netpickz.core.movie.entity;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.common.entity.CertificationEntity;
import com.netpickz.core.movie.entity.pk.MoviePk;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
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

	@Column(name="poster_path")
	private String posterPath  ;

	@Column(name="release_date")
	private String releaseDate   ;
	
    @OneToOne
    @JoinColumn(name = "certification_id", nullable = true)
    private CertificationEntity certificationEntity;
	
	@Column(name="created_at")
	@CreationTimestamp
	private Timestamp createdAt;
	
	
}
