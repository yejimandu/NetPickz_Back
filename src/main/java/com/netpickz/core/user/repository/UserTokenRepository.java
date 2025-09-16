package com.netpickz.core.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.netpickz.core.user.entity.UserEntity;
import com.netpickz.core.user.entity.UserTokenEntity;

//@Repository
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, String>{
	
}
