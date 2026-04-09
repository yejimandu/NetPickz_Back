package com.netpickz.core.user.repository;

import static com.netpickz.core.user.entity.QUserEntity.userEntity;
import static com.netpickz.core.user.entity.QUserInfoEntity.userInfoEntity;

import java.util.Optional;

import org.springframework.util.StringUtils;

import com.netpickz.common.enumType.StateType;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.entity.UserInfoEntity;
import com.netpickz.core.user.mapper.UserMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final UserMapper userMapper;

	@Override
	public Optional<UserDTO> findByUserId(String userId) {
		var userInfo = queryFactory
				.selectFrom(userInfoEntity)
				.join(userInfoEntity.userEntity, userEntity)
				.fetchJoin()
				.where(userIdEq(userId), isActive())
				.fetchOne();
		
		return Optional.ofNullable(userInfo)
				.map(userMapper::userToUserDTO);
	}

	@Override
	@Transactional
	public void upsert(UserInfoEntity userInfo) {
		var update = queryFactory.update(userInfoEntity);
		
		if(userInfo.getName() != null && !userInfo.getName().isBlank()) {
			update.set(userInfoEntity.name, userInfo.getName());
		}
		if(userInfo.getEmail() != null && !userInfo.getEmail().isBlank()) {
			update.set(userInfoEntity.email, userInfo.getEmail());
		}
		if(userInfo.getPassword() != null && !userInfo.getPassword().isBlank()) {
			update.set(userInfoEntity.password, userInfo.getPassword());
		}
		// TODO
		if(userInfo.isEmailVerified()) {
			update.set(userInfoEntity.emailVerified, userInfo.isEmailVerified());
		}
		if(!update.isEmpty()) {
			update
			.where(userIdEq(userInfo.getUserId()))
			.execute();
		}
	}

	@Override
	@Transactional
	public void updateStateByUserId(String userId, StateType type) {
		queryFactory
		.update(userInfoEntity)
		.set(userInfoEntity.state, type)
		.where(userIdEq(userId))
		.execute();
	}

	@Override
	@Transactional
	public void updateEmailVerifiedByEmail(String email, boolean value) {
		queryFactory
			.update(userInfoEntity)
			.set(userInfoEntity.emailVerified, value)
			.where(emailEq(email))
		.execute();
	}
	
	private BooleanExpression userIdEq(String userId) {
		return StringUtils.hasText(userId) ? userInfoEntity.userEntity.userId.eq(userId) : null;
	}

	private BooleanExpression emailEq(String email) {
		return StringUtils.hasText(email) ? userInfoEntity.email.eq(email) : null;
	}

	private BooleanExpression isActive() {
		return userInfoEntity.state.eq(StateType.정상);
	}
}
