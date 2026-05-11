package com.netpickz.core.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String>{
	
	Optional<UserEntity> findById(String userId);
}
