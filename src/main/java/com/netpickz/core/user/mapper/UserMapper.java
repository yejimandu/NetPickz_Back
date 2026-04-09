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
	
	 @Mapping(target = "sessionId", ignore = true)
	 @Mapping(target = "name", ignore = true)
	 @Mapping(target = "email", ignore = true)
	 @Mapping(target = "movieId", ignore = true)
	 @Mapping(target = "state", ignore = true)
	 @Mapping(target = "password", ignore = true)
	 UserDTO userToUserDTO(UserEntity userEntity);
	 
	 @Mapping(source = "userEntity.userId" , target = "userId")
	 @Mapping(target = "sessionId", ignore = true)
	 @Mapping(target = "movieId", ignore = true)
	 UserDTO userToUserDTO(UserInfoEntity userInfoEntity);
	 
//	 @Mapping(source = "userEntity.userId", target = "userId")
//	 @Mapping(source = "movieEntity.movieId", target = "movieId")
//	 RatingDTO userToUserDTO(UserRatingInfoEntity UserRatingInfoEntity);
//	 List<RatingDTO> userToUserDTO(List<UserRatingInfoEntity> UserRatingInfoEntitys );
	 
	 
	 @Mapping(source = "userEntity.userId", target = "userId")
	 @Mapping(source = "movieEntity.movieId", target = "movieId")
	 @Mapping(source = "movieEntity.title", target = "title")
	 @Mapping(source = "movieEntity.movieInfo.posterPath", target = "posterPath")
	 RatingDTO entityToDTO(UserRatingInfoEntity UserRatingInfoEntity);
	 List<RatingDTO> entityToDTO(List<UserRatingInfoEntity> UserRatingInfoEntitys );
	 
}
