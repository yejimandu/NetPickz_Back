package com.netpickz.core.user.service;

import java.util.List;
import java.util.Optional;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.enumType.StateType;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.user.dto.UserDTO;

public interface UserService {

	Optional<RatingDTO> addRatingByUser(String movieId, RatingRequest request);

	Optional<UserDTO> createUser(UserRequest request);

	Optional<UserDTO> getUserInfoByUserId(String userId);

	Optional<UserDTO> updateUser(UserRequest request);

	void deleteRatingByUser(String movieId, String sessionId);

	Optional<List<RatingDTO>> getHistoryByUserId(String userId);

	String updateUserState(String userId, StateType type);

	void updateEmailVerified(String email, boolean value);

}
