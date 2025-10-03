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
@Embeddable
@Builder
@Data
public class MovieProviderPK implements Serializable{

	@Column(name = "movie_id")
	private String movieId;
	
	@Column(name = "provider_id")
	private String providerId;
}
