package com.netpickz.core.user;

import java.util.List;
import java.util.Optional;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.enumType.StateType;
import com.netpickz.core.movie.RatingDTO;

public interface UserService {

//	Optional<UserDTO> addRatingByUser(UserDTO userDTO, Double rationg);
	Optional<RatingDTO> addRatingByUser(String movieId, RatingRequest request);

	Optional<UserDTO> createUser(UserRequest request);

	Optional<UserDTO> getUserInfoByUserId(String userId);

	Optional<UserDTO> updateUser(UserRequest request);

	void deleteRatingByUser(String movieId, String sessionId);

	Optional<List<UserDTO>> getHistoryByUserId(String userId);

	int updateUserState(String userId, StateType type);

}
