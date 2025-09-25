package com.netpickz.core.movie.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name="movies")
public class MovieEntity {
	
	@Id
	@Column(name="movie_id")
	private String movieId ;
	
	@Column(unique = true)
	private String id;
	
	@Column
	private String title;
	
	@Column(name="created_at")
	private Timestamp createdAt;
	
    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default //
    private List<MovieGenreEntity> genrees = new ArrayList();

    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 
    private List<MovieProviderEntity> providers = new ArrayList();
	
}
