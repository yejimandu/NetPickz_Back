package com.netpickz.core.movie.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.netpickz.common.entity.GenreEntity;
import com.netpickz.core.movie.entity.pk.MovieGenrePK;

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
@Table(name="movie_genre")
@Builder
@ToString(exclude = {"movieEntity", "genreEntity"})
public class MovieGenreEntity {

	@EmbeddedId
	private MovieGenrePK id;
	
    @ManyToOne
    @MapsId("movieId") // PK의 movieId와 매핑
    @JoinColumn(name="movie_id")
    private MovieEntity movieEntity;
	
    @ManyToOne
    @MapsId("genreId") // PK의 genreId와 매핑
    @JoinColumn(name = "genre_id")
    private GenreEntity genreEntity;
	
	@Column(name="created_at",  nullable = false)
	@CreationTimestamp
	private Timestamp createdAt;
	
}
