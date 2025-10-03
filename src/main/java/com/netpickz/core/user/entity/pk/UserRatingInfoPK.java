package com.netpickz.core.user.entity.pk;

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
public class UserRatingInfoPK implements Serializable{

	@Column(name="user_id")
	private String userId;
	
	@Column(name="movie_id")
	private String movieId;
}
