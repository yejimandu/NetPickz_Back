package com.netpickz.core.movie.entity.pk;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Data
public class MovieGenrePK implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name = "movie_id")
	private String movieId;
	
	@Column(name = "genre_id")
	private String genreId;
}
