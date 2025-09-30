package com.netpickz.core.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.user.entity.UserInfoEntity;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String>{
	
}
