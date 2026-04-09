package com.netpickz.core.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.user.entity.UserRatingInfoEntity;
import com.netpickz.core.user.entity.pk.UserRatingInfoPK;

public interface UserRatingInfoRepository extends JpaRepository<UserRatingInfoEntity, UserRatingInfoPK>, UserRatingInfoRepositoryCustom{
	List<UserRatingInfoEntity> findByIdUserId(String userId);
}
