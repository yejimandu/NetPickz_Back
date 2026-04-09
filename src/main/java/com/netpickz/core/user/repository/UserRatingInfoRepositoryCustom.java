package com.netpickz.core.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.stats.dto.RatingStatsDTO;
import com.netpickz.core.stats.dto.UserStatsDTO;

public interface UserRatingInfoRepositoryCustom {
	Page<RatingDTO> findRatingByUserId( String userId, String keyword, Pageable pageable);
	Optional<RatingStatsDTO> findRatingCountsByUserId(String userId);
	Optional<UserStatsDTO> findStatsByUserd(String userId);
}
