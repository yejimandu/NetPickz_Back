package com.netpickz.core.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.auth.entity.UserTokensEntity;

public interface UserTokensRepository  extends JpaRepository<UserTokensEntity, String>{

}
