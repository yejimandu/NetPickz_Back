package com.netpickz.core.stats.service;

import java.util.Optional;

import com.netpickz.core.stats.dto.RatingStatsDTO;
import com.netpickz.core.stats.dto.UserStatsDTO;

public interface StatsSevice {

	Optional<RatingStatsDTO> getUserRatingStats(String userId);
	Optional<UserStatsDTO> getUserStats(String userId);

}
