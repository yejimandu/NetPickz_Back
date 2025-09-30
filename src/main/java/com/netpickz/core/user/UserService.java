package com.netpickz.core.user;

import java.util.Optional;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;

public interface UserService {

//	Optional<UserDTO> addRatingByUser(UserDTO userDTO, Double rationg);
	void addRatingByUser(String movieId, RatingRequest request);

	Optional<UserDTO> createUser(UserRequest request);

	Optional<UserDTO> getUserInfoByUserId(String userId);

	Optional<UserDTO> updateUser(UserRequest request);

}
