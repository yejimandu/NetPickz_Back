package com.netpickz.core.user.repository;

import java.util.Optional;

import com.netpickz.common.enumType.StateType;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.entity.UserInfoEntity;

public interface UserRepositoryCustom {

	Optional<UserDTO> findByUserId(String userId);

	void  upsert(UserInfoEntity userInfo);

	void updateStateByUserId(String userId, StateType type);


}
