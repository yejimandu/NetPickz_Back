package com.netpickz.core.movie.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.common.entity.ProvidersEntity;
import com.netpickz.core.movie.entity.pk.MovieProviderPK;

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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="movie_provider")
@Builder
public class MovieProviderEntity {

	@EmbeddedId
	private MovieProviderPK id;
	
	@ManyToOne
	@MapsId("movieId") 
    @JoinColumn(name = "movie_id")
    private MovieEntity movieEntity;
	
	@ManyToOne
    @MapsId("providerId") 
    @JoinColumn(name = "provider_id")
    private ProvidersEntity providersEntity;
    
    @Column(name="created_at", nullable = false)
    @UpdateTimestamp
	private Timestamp createdAt;
}
