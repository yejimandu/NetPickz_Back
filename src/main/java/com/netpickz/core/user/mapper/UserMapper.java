package com.netpickz.core.user.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.entity.UserEntity;
import com.netpickz.core.user.entity.UserInfoEntity;
import com.netpickz.core.user.entity.UserRatingInfoEntity;


@Mapper(componentModel = "spring")
public interface UserMapper {
	
	 UserDTO userToUserDTO(UserEntity userEntity);
	 UserDTO userToUserDTO(UserInfoEntity userInfoEntity);
	 UserDTO userToUserDTO(UserRatingInfoEntity UserRatingInfoEntity);
	 
	 @Mapping(source = "userEntity.userId", target = "userId")
	 @Mapping(source = "movieEntity.movieId", target = "movieId")
	 @Mapping(source = "guestSessionId", target = "sessionId")
	 List<RatingDTO> userToUserDTO(List<UserRatingInfoEntity> UserRatingInfoEntitys );
}
