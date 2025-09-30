package com.netpickz.core.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netpickz.api.movie.request.RatingRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.session.SessionService;
import com.netpickz.core.user.entity.UserEntity;
import com.netpickz.core.user.entity.UserInfoEntity;
import com.netpickz.core.user.entity.UserRatingInfoEntity;
import com.netpickz.core.user.repository.UserInfoRepository;
import com.netpickz.core.user.repository.UserRatingInfoRepository;
import com.netpickz.core.user.repository.UserRepository;
import com.netpickz.core.user.repository.UserRepositoryCustom;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRatingInfoRepository userRatingInfoRepository;

	@Autowired
	private UserInfoRepository userInfoRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRepositoryCustom userRepositoryCustom;
	
	@Autowired
	private SessionService sessionService;
	
//	public Optional<UserDTO> addRatingByUser(UserDTO userDTO, Double rating) {
	@Override
	public void addRatingByUser(String movieId, RatingRequest request) {
		//TODO get 아닌 경우 처리 필요
		var dto = sessionService.getSessionInfo(request.getSessionId()).get();
		System.out.println(dto);
		System.out.println(request);
		userRatingInfoRepository.save(UserRatingInfoEntity.builder()
				.movieEntity(MovieEntity.builder().movieId(movieId).build())
				.rating(request.getValue().floatValue())
				.userEntity(UserEntity.builder().userId(dto.getUserId()).build())
				.guestSessionId(request.getSessionId())
				.build());
	}

	@Override
	public Optional<UserDTO> createUser(UserRequest request) {
		var userEntity = UserEntity.builder().userId(request.getUserId()).build();
		var users = userRepository.saveAndFlush(userEntity);
		var userInfo = UserInfoEntity.builder().userEntity(users).name(request.getName()).build();
		userInfoRepository.save(userInfo);
		
		var userDto = UserDTO.builder().userId(users.getUserId()).name(userInfo.getName()).build();
		
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
	
}
