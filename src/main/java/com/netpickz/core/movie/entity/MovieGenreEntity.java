package com.netpickz.core.movie.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.netpickz.common.entity.GenreEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="movie_genre")
@Builder
public class MovieGenreEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @ManyToOne
    @JoinColumn(name="movie_id", nullable = true)
    private MovieEntity movieEntity;
	
    @OneToOne
    @JoinColumn(name = "genre_id", nullable = true)
    private GenreEntity genreEntity;
	
	@Column(name="created_at")
	@CreationTimestamp
	private Timestamp createdAt;
}
