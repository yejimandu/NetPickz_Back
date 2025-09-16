package com.netpickz.core.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.netpickz.core.user.entity.UserEntity;

//@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>{
	
	Optional<UserEntity> findById(String userId);
}
