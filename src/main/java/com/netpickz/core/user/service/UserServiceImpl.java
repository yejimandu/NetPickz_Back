package com.netpickz.core.user.service;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.constants.Constants;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.enumType.StateType;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.core.movie.dto.PageDTO;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.session.service.SessionService;
import com.netpickz.core.stats.dto.RatingStatsDTO;
import com.netpickz.core.stats.dto.UserStatsDTO;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.entity.UserEntity;
import com.netpickz.core.user.entity.UserInfoEntity;
import com.netpickz.core.user.entity.UserRatingInfoEntity;
import com.netpickz.core.user.entity.pk.UserRatingInfoPK;
import com.netpickz.core.user.mapper.UserMapper;
import com.netpickz.core.user.repository.UserInfoRepository;
import com.netpickz.core.user.repository.UserRatingInfoRepository;
import com.netpickz.core.user.repository.UserRepository;

import io.lettuce.core.RedisException;
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
	private final SessionService sessionService;
	private final StringRedisTemplate redisTemplate;

	@Override
	public Optional<RatingDTO> addRatingByUser(String movieId, RatingRequest request) {
		var rating = request.getValue();
		log.debug("Adding rating: movieId={}, sessionId={}, rating={}" , movieId , request.getSessionId(), request.getValue());
		var sessionDto = sessionService.getSessionInfo(request.getSessionId())
				.orElseThrow(() -> new NetPickzException(ErrorCode.SESSION_NOT_FOUND));
		
		 userRatingInfoRepository.save(UserRatingInfoEntity.builder()
				.id(UserRatingInfoPK.builder().userId(sessionDto.getUserId()).movieId(movieId).build())
				.rating(rating.floatValue())
				.sessionId(request.getSessionId())
				.movieEntity(MovieEntity.builder().movieId(movieId).build())
				.userEntity(UserEntity.builder().userId(sessionDto.getUserId()).build())
				.build()
				);
		
		log.info("Rating saved: movieId={}, userId={}, rating={}", movieId, sessionDto.getUserId(), rating);
		return Optional.ofNullable(RatingDTO.builder()
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
		var users = userRepository.save(userEntity);
		var encodePw = passwordEncoder.encode(request.getPassword());
		var userInfo =  userInfoRepository.save(UserInfoEntity.builder().userEntity(users).name(request.getName())
				.password(encodePw)
				.state(StateType.정상)
				.email(request.getEmail())
				.emailVerified(false)
				.build());
		var userDto = userMapper.userToUserDTO(userInfo);
		log.info("Creating saved: userId={}, name={}, email={}", userDto.getUserId(), userDto.getName(), userDto.getEmail());
		return Optional.ofNullable(userDto);
	}

	@Override
	public Optional<UserDTO> getUserInfoByUserId(String userId) {
		log.debug("Find user info: userId={}" , userId);
		return userInfoRepository.findByUserId(userId);
	}

	@Override
	public Optional<UserDTO> updateUser(UserRequest request) {
		log.debug("Update user info: userId={}" , request.getUserId());
		
		var userInfo = UserInfoEntity.builder()
				.userId(request.getUserId())
				.name(request.getName())
				.email(request.getEmail())
				.password(request.getPassword())
				.build();

		if(request.getEmail() != null && !request.getEmail().isBlank()) {
			userInfo.setEmailVerified(request.getEmailVerified());
		}
		
		userInfoRepository.upsert(userInfo);
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
	public PageDTO<RatingDTO> getHistoryByUserId(String userId, String keyword, Pageable pageable ) {
		log.debug("Find User Rating History info: userId={}" , userId);
		getUserInfoByUserId(userId)
			.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND)); // userID 있는지 체크 

		var ratingDtos = userRatingInfoRepository.findRatingByUserId(userId, keyword,  pageable);
		var result = PageDTO.<RatingDTO>builder()
			.totalCount((int) ratingDtos.getTotalElements())
			.totalPage(ratingDtos.getTotalPages())
			.result(ratingDtos.getContent())
		.build();
		log.info("User Rating History info Found: userId={}, totalCount={}, totalPage={}", userId, result.getTotalCount(), result.getTotalPage());
		return result;
	}

	@Override
	public String updateUserState(String userId, StateType type) {
		log.debug("Update User State: userId={}, type={}" , userId, type);
		userInfoRepository.updateStateByUserId(userId, type);
		log.info("Update User State Succesed: userId={}, type={}", userId, type);
		return Constants.UPDATE_SUCCESS;
	}

	@Override
	public void updateEmailVerified(String email, boolean value) {
		log.debug("Update User Email Verified : email={}, value={}" ,email, value);
		userInfoRepository.updateEmailVerifiedByEmail(email, value);
	}

	@Override
	public Optional<RatingDTO> getRatingByUser(String movieId, String userId) {
		log.debug("Find User Rating Info : movieId={}, userId={}" ,movieId, userId);
		var userRatings = userRatingInfoRepository.findById(UserRatingInfoPK.builder().userId(userId).movieId(movieId).build())
				.orElseThrow(() -> new NetPickzException(ErrorCode.RATING_NOT_FOUND));
		var ratingDto = userMapper.entityToDTO(userRatings);
		return Optional.ofNullable(ratingDto);
	}

	@Override
	public Optional<RatingStatsDTO> getUserRatingCounts(String userId) {
		log.debug("Find User Rating Counts Info : userId={}" , userId);
		return userRatingInfoRepository.findRatingCountsByUserId(userId);	
	}

	@Override
	public Optional<UserStatsDTO> getUserStats(String userId) {
		log.debug("Find User Stats Info : userId={}" , userId);
		return userRatingInfoRepository.findStatsByUserd(userId);
	}

	@Override
	public String passwordChange(String userId, String newPassword) {
		log.debug("Reset User Password Info : userId={}, newPassword={}", userId, newPassword);
		var msg = Constants.PASSWORD_RESET_FAIL;
		var encodeNewPw = passwordEncoder.encode(newPassword);
		var updatedUser = updateUser(UserRequest.builder().userId(userId).password(encodeNewPw).build());
		if(updatedUser.isPresent()) {
			msg = Constants.PASSWORD_RESET_SUCCESS;
			redisTemplate.delete("pwReset:" + userId);
		}
		return msg;
	}

	@Override
	public String pwResetVerify(String userId, String token) {
		log.debug("Reset User Password Url Verify : userId={}, token={}", userId, token);
		try {
			var saved = redisTemplate.opsForValue().get("pwReset:" + userId);
			if(saved == null ||  !saved.equals(token)) {
				throw new NetPickzException(ErrorCode.TOKEN_INVALID);
			}
			return Constants.VERIFY_SUCCESS;
		}catch (RedisException e) {
			log.error("Fail to Connect Redis. pwReset:  userId={}, token={}, msg={}", userId, token , e.getMessage(), e);
			throw new NetPickzException(ErrorCode.REDIS_CONNECT_FAIL);
		}
	}
}
