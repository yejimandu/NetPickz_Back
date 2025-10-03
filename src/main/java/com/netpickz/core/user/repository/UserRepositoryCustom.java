package com.netpickz.core.user.repository;

import java.util.Optional;

import com.netpickz.core.user.UserDTO;
import com.netpickz.core.user.entity.UserInfoEntity;

public interface UserRepositoryCustom {

	Optional<UserDTO> findByUserId(String userId);

	void  upsert(UserInfoEntity userInfo);

}
