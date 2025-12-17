package com.netpickz.core.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
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

	private final PasswordEncoder passwordEncoder;
	private final UserRatingInfoRepository userRatingInfoRepository;
	private final UserInfoRepository userInfoRepository;
	private final UserRepository userRepository;
	private final UserRepositoryCustom userRepositoryCustom;
	private final SessionService sessionService;

	@Override
	public Optional<RatingDTO> addRatingByUser(String movieId, RatingRequest request) {
		//TODO get 아닌 경우 처리 필요
		log.debug("Adding rating: movieId={}, sessionId={}, rating={}" , movieId , request.getSessionId(), request.getValue());
		try {
			var sessionDto = sessionService.getSessionInfo(request.getSessionId())
					.orElseThrow(() -> new NetPickzException(ErrorCode.SESSION_NOT_FOUND));
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
			log.info("Rating saved: movieId={}, userId={}, rating={}", 
		                 movieId, sessionDto.getUserId(), request.getValue());
			return Optional.of(ratingDTO);
		} catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		} catch (Exception e) {
			log.error("Failed to save rating info : {}", e.getMessage() , e);
			throw new NetPickzException(ErrorCode.SERVER_ERROR);
		}
	}

	// TODO 아마 여기서 이메일 저장 안해서 확인해야해,
	@Override
	public Optional<UserDTO> createUser(UserRequest request) {
		log.debug("Create user info: userId={}, name={}, email={}" , request.getUserId() , request.getName(), request.getEmail());
		try {
			var userEntity = UserEntity.builder().userId(request.getUserId()).build();
			var users = userRepository.saveAndFlush(userEntity);
			var encodePw = passwordEncoder.encode(request.getPassword());
			var userInfo =  userInfoRepository.save(UserInfoEntity.builder().userEntity(users).name(request.getName())
					.password(encodePw)
					.state(StateType.정상)
					.email(request.getEmail())
					.emailVerified(false)
					.build());
			var userDto = UserDTO.builder()
					.userId(users.getUserId())
					.name(userInfo.getName())
					.email(userInfo.getEmail())
					.state(userInfo.getState().toString())
					.build();
			log.info("Creating saved: userId={}, name={}, email={}", userDto.getUserId(), userDto.getName(), userDto.getEmail());
			return Optional.of(userDto);
		} catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		} catch (Exception e) {
			log.error("Failed to create User Info : {}", e.getMessage() , e);
			throw new NetPickzException(ErrorCode.SERVER_ERROR);
		}
	}

	@Override
	public Optional<UserDTO> getUserInfoByUserId(String userId) {
		log.debug("Find user info: userId={}" , userId);
		try {
			return userRepositoryCustom.findByUserId(userId);
		} catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		} catch (Exception e) {
			log.error("Failed to find User Info : {}", e.getMessage() , e);
			throw new NetPickzException(ErrorCode.USER_NOT_FOUND);
		}
	}

	@Override
	public Optional<UserDTO> updateUser(UserRequest request) {
		log.debug("Update user info: userId={}" , request.getUserId());
		try {
			var userInfo = UserInfoEntity.builder().userEntity(UserEntity.builder().userId(request.getUserId()).build())
					.name(request.getName()).build();
			userRepositoryCustom.upsert(userInfo);
			return getUserInfoByUserId(request.getUserId());
		} catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		} catch (Exception e) {
			log.error("Failed to update User Info : {}", e.getMessage() , e);
			throw new NetPickzException(ErrorCode.SERVER_ERROR);
		}
	}

	@Override
	public void deleteRatingByUser(String movieId, String sessionId) {
		log.debug("Delete User Rating info: movieId={}, sessionId={}" , movieId, sessionId);
		try {
			var sessionDto = sessionService.getSessionInfo(sessionId)
					.orElseThrow(() -> new NetPickzException(ErrorCode.SESSION_NOT_FOUND));
			userRatingInfoRepository.deleteById(UserRatingInfoPK.builder()
												.userId(sessionDto.getUserId())
												.movieId(movieId)
												.build());
			 log.info("Rating deleted: movieId={}, userId={}", movieId, sessionDto.getUserId());
		} catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		} catch (Exception e) {
			log.error("Failed to delete User Rating Info : {}", e.getMessage() , e);
			throw new NetPickzException(ErrorCode.SERVER_ERROR);
		}
	}

	@Override
	public Optional<List<UserDTO>> getHistoryByUserId(String userId) {
		log.debug("Find User Rating History info: userId={}" , userId);
		getUserInfoByUserId(userId).orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND)); // userID 있는지 체크 
		try {
			
			var userRatingEntity = userRatingInfoRepository.findByIdUserId(userId);
			var userDtos = userRatingEntity.stream().map(e -> UserDTO.builder()
					.userId(e.getUserEntity().getUserId())
					.movieId(e.getMovieEntity().getMovieId())
					.sessionId(e.getGuestSessionId())
					.build())
					.toList();
			log.info("User Rating History info Found: movieId={}, count={}", userId, userDtos.size());
			return Optional.of(userDtos);
		}catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		}catch (Exception e) {
			log.error("Failed to Find User Rating Info : {}", e.getMessage() , e);
			throw new NetPickzException(ErrorCode.SERVER_ERROR);
		}
	}

	@Override
	public int updateUserState(String userId, StateType type) {
		log.debug("Update User State: userId={}, type={}" , userId, type);
		try {
			userRepositoryCustom.updateStateByUserId(userId, type);
			// TODO get 아닌 것도 체크
			var user = userInfoRepository.findById(userId)
					.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND));
			log.info("Update User State Succesed: userId={}, type={}", userId, user.getState());
			return type.equals(user.getState()) ? 1 : 0;
		} catch (DataAccessException e) {
			throw new NetPickzException(ErrorCode.DATABASE_ERROR);
		} catch (Exception e) {
			log.error("Failed to update User State : {}", e.getMessage() , e);
			throw new NetPickzException(ErrorCode.SERVER_ERROR);
		}
	}
}
