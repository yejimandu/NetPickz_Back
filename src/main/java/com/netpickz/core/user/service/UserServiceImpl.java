package com.netpickz.core.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.enumType.StateType;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.session.service.SessionService;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.entity.UserEntity;
import com.netpickz.core.user.entity.UserInfoEntity;
import com.netpickz.core.user.entity.UserRatingInfoEntity;
import com.netpickz.core.user.entity.pk.UserRatingInfoPK;
import com.netpickz.core.user.repository.UserInfoRepository;
import com.netpickz.core.user.repository.UserRatingInfoRepository;
import com.netpickz.core.user.repository.UserRepository;
import com.netpickz.core.user.repository.UserRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRatingInfoRepository userRatingInfoRepository;
	private final UserInfoRepository userInfoRepository;
	private final UserRepository userRepository;
	private final UserRepositoryCustom userRepositoryCustom;
	private final SessionService sessionService;

	@Override
	public Optional<RatingDTO> addRatingByUser(String movieId, RatingRequest request) {
		//TODO get 아닌 경우 처리 필요
		var sessionDto = sessionService.getSessionInfo(request.getSessionId()).get();
		userRatingInfoRepository.save(UserRatingInfoEntity.builder()
				.id(UserRatingInfoPK.builder().userId(sessionDto.getUserId()).movieId(movieId).build())
				.rating(request.getValue().floatValue())
				.guestSessionId(request.getSessionId())
				.movieEntity(MovieEntity.builder().movieId(movieId).build())
				.userEntity(UserEntity.builder().userId(sessionDto.getUserId()).build())
				.build()
				);
		
		var ratingDTO = RatingDTO.builder()
				.movieId(movieId)
				.userId(sessionDto.getUserId())
				.rating(request.getValue())
				.sessionId(request.getSessionId())
				.build();
		return Optional.of(ratingDTO);
	}

	@Override
	public Optional<UserDTO> createUser(UserRequest request) {
		var userEntity = UserEntity.builder().userId(request.getUserId()).build();
		var users = userRepository.saveAndFlush(userEntity);
		
		var encodePw = passwordEncoder.encode(request.getPassword());
		var userInfoEntity = UserInfoEntity.builder().userEntity(users).name(request.getName())
				.password(encodePw)
				.state(StateType.정상)
				.email(request.getEmail())
				.emailVerified(false)
				.build();
		var userInfo =  userInfoRepository.save(userInfoEntity);
		
		var userDto = UserDTO.builder()
				.userId(users.getUserId())
				.name(userInfo.getName())
				.email(userInfo.getEmail())
				.state(userInfo.getState().toString())
				.build();
		
		return Optional.of(userDto);
	}

	@Override
	public Optional<UserDTO> getUserInfoByUserId(String userId) {
		return userRepositoryCustom.findByUserId(userId);
	}

	@Override
	public Optional<UserDTO> updateUser(UserRequest request) {
		var userInfo = UserInfoEntity.builder().userEntity(UserEntity.builder().userId(request.getUserId()).build())
				.name(request.getName()).build();
		userRepositoryCustom.upsert(userInfo);
		
		return getUserInfoByUserId(request.getUserId());
	}

	@Override
	public void deleteRatingByUser(String movieId, String sessionId) {
		var sessionDto = sessionService.getSessionInfo(sessionId).get();
		userRatingInfoRepository.deleteById(UserRatingInfoPK.builder().userId(sessionDto.getUserId()).movieId(movieId).build());
	}

	@Override
	public Optional<List<UserDTO>> getHistoryByUserId(String userId) {
		// TODO 널 체크
		var userRatingEntity = userRatingInfoRepository.findByIdUserId(userId);
		var userDto = userRatingEntity.stream().map(e -> UserDTO.builder()
						.userId(e.getUserEntity().getUserId())
						.movieId(e.getMovieEntity().getMovieId())
						.sessionId(e.getGuestSessionId())
						.build())
						.toList();
		return Optional.of(userDto);
	}

	@Override
	public int updateUserState(String userId, StateType type) {
		System.out.println(type.getValue());
		userRepositoryCustom.updateStateByUserId(userId, type);
		// TODO get 아닌 것도 체크
		var user = userInfoRepository.findById(userId).get();
		
		return type.equals(user.getState()) ? 1 : 0;
	}

}
