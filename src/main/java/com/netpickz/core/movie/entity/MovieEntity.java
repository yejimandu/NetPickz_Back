package com.netpickz.core.movie.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.core.user.entity.UserRatingInfoEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name="movies")
@ToString(exclude = {"movieInfo" , "genres" , "providers" ,  "ratings" } )
public class MovieEntity {
	
	@Id
	@Column(name="movie_id")
	private String movieId ;
	
	@Column(unique = true, nullable = false)
	private String id;

	@Column(nullable = false)
	private String title;
	
	@Column(name="created_at", nullable = false)
	@UpdateTimestamp
	private Timestamp createdAt;
	
	@OneToOne(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true
			,fetch = FetchType.LAZY)
    private MovieInfoEntity movieInfo;
	
    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true
    		,fetch = FetchType.LAZY)
    @Builder.Default //
    private List<MovieGenreEntity> genres = new ArrayList();

    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true
    		,fetch = FetchType.LAZY)
    @Builder.Default // 
    private List<MovieProviderEntity> providers = new ArrayList();
    
    @OneToMany(mappedBy = "movieEntity", cascade = CascadeType.ALL, orphanRemoval = true
    		,fetch = FetchType.LAZY)
    @Builder.Default //
    private List<UserRatingInfoEntity> ratings = new ArrayList();
	
}
