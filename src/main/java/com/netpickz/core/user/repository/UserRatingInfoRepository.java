package com.netpickz.core.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.user.entity.UserRatingInfoEntity;

public interface UserRatingInfoRepository extends JpaRepository<UserRatingInfoEntity, String>{
	
}
