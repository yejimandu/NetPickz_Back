package com.netpickz.core.user.service;

import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.enumType.StateType;
import com.netpickz.core.movie.dto.PageDTO;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.stats.dto.RatingStatsDTO;
import com.netpickz.core.stats.dto.UserStatsDTO;
import com.netpickz.core.user.dto.UserDTO;

public interface UserService {

	Optional<RatingDTO> addRatingByUser(String movieId, RatingRequest request);

	Optional<UserDTO> createUser(UserRequest request);

	Optional<UserDTO> getUserInfoByUserId(String userId);

	Optional<UserDTO> updateUser(UserRequest request);

	void deleteRatingByUser(String movieId, String sessionId);

	PageDTO<RatingDTO> getHistoryByUserId(String userId ,String keyword , Pageable pageable );

	String updateUserState(String userId, StateType type);

	void updateEmailVerified(String email, boolean value);

	Optional<RatingDTO> getRatingByUser(String movieId, String userId);

	Optional<RatingStatsDTO> getUserRatingCounts(String userId);

	Optional<UserStatsDTO> getUserStats(String userId);

	String passwordChange(String userId, String newPassword);

	String pwResetVerify(String userId, String token);

}
