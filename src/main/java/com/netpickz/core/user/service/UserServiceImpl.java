package com.netpickz.core.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.enumType.StateType;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.session.service.SessionService;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.entity.UserEntity;
import com.netpickz.core.user.entity.UserInfoEntity;
import com.netpickz.core.user.entity.UserRatingInfoEntity;
import com.netpickz.core.user.entity.pk.UserRatingInfoPK;
import com.netpickz.core.user.mapper.UserMapper;
import com.netpickz.core.user.repository.UserInfoRepository;
import com.netpickz.core.user.repository.UserRatingInfoRepository;
import com.netpickz.core.user.repository.UserRepository;
import com.netpickz.core.user.repository.UserRepositoryCustom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final UserRatingInfoRepository userRatingInfoRepository;
	private final UserInfoRepository userInfoRepository;
	private final UserRepository userRepository;
	private final UserRepositoryCustom userRepositoryCustom;
	private final SessionService sessionService;

	@Override
	public Optional<RatingDTO> addRatingByUser(String movieId, RatingRequest request) {
		var rating = request.getValue();
		log.debug("Adding rating: movieId={}, sessionId={}, rating={}" , movieId , request.getSessionId(), request.getValue());
		var sessionDto = sessionService.getSessionInfo(request.getSessionId())
				.orElseThrow(() -> new NetPickzException(ErrorCode.SESSION_NOT_FOUND));
		
		 userRatingInfoRepository.save(UserRatingInfoEntity.builder()
				.id(UserRatingInfoPK.builder().userId(sessionDto.getUserId()).movieId(movieId).build())
				.rating(rating.floatValue())
				.guestSessionId(request.getSessionId())
				.movieEntity(MovieEntity.builder().movieId(movieId).build())
				.userEntity(UserEntity.builder().userId(sessionDto.getUserId()).build())
				.build()
				);
		
		log.info("Rating saved: movieId={}, userId={}, rating={}", movieId, sessionDto.getUserId(), rating);
		// TODO 
		return Optional.of(RatingDTO.builder()
				.movieId(movieId)
				.userId(sessionDto.getUserId())
				.rating(rating)
				.sessionId(sessionDto.getSessionId())
				.build());
	}

	// TODO 아마 여기서 이메일 저장 안해서 확인해야해,
	@Override
	public Optional<UserDTO> createUser(UserRequest request) {
		log.debug("Create user info: userId={}, name={}, email={}" , request.getUserId() , request.getName(), request.getEmail());
		var userEntity = UserEntity.builder().userId(request.getUserId()).build();
		var users = userRepository.saveAndFlush(userEntity);
		var encodePw = passwordEncoder.encode(request.getPassword());
		var userInfo =  userInfoRepository.save(UserInfoEntity.builder().userEntity(users).name(request.getName())
				.password(encodePw)
				.state(StateType.정상)
				.email(request.getEmail())
				.emailVerified(false)
				.build());
		var userDto = userMapper.userToUserDTO(userInfo);
		log.info("Creating saved: userId={}, name={}, email={}", userDto.getUserId(), userDto.getName(), userDto.getEmail());
		return Optional.of(userDto);
	}

	@Override
	public Optional<UserDTO> getUserInfoByUserId(String userId) {
		log.debug("Find user info: userId={}" , userId);
		return userRepositoryCustom.findByUserId(userId);
	}

	@Override
	public Optional<UserDTO> updateUser(UserRequest request) {
		log.debug("Update user info: userId={}" , request.getUserId());
		var userInfo = UserInfoEntity.builder()
				.userEntity(UserEntity.builder().userId(request.getUserId()).build())
				.name(request.getName()).build();
		userRepositoryCustom.upsert(userInfo);
		return getUserInfoByUserId(request.getUserId());
	}

	@Override
	public void deleteRatingByUser(String movieId, String sessionId) {
		log.debug("Delete User Rating info: movieId={}, sessionId={}" , movieId, sessionId);
		var sessionDto = sessionService.getSessionInfo(sessionId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.SESSION_NOT_FOUND));
		userRatingInfoRepository.deleteById(UserRatingInfoPK.builder()
											.userId(sessionDto.getUserId())
											.movieId(movieId)
											.build());
		 log.info("Rating deleted: movieId={}, userId={}", movieId, sessionDto.getUserId());
	}

	@Override
	public Optional<List<RatingDTO>> getHistoryByUserId(String userId) {
		log.debug("Find User Rating History info: userId={}" , userId);
		getUserInfoByUserId(userId)
			.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND)); // userID 있는지 체크 
		var userRatingEntity = userRatingInfoRepository.findByIdUserId(userId);
		var ratingDtos = userMapper.userToUserDTO(userRatingEntity);
		log.info("User Rating History info Found: userId={}, count={}", userId, ratingDtos.size());
		return Optional.of(ratingDtos);
	}

	@Override
	public int updateUserState(String userId, StateType type) {
		log.debug("Update User State: userId={}, type={}" , userId, type);
		userRepositoryCustom.updateStateByUserId(userId, type);
		var user = userInfoRepository.findById(userId)
				.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND));
		log.info("Update User State Succesed: userId={}, type={}", userId, user.getState());
		return type.equals(user.getState()) ? 1 : 0;
	}
}
