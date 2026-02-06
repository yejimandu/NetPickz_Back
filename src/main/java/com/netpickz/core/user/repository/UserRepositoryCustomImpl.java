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
		UserInfoEntity userInfo = queryFactory
				.selectFrom(userInfoEntity)
				.join(userInfoEntity.userEntity, userEntity)
				.fetchJoin()
				.where(userEntity.userId.eq(userId))
				.fetchOne();
		
		return Optional.ofNullable(userInfo)
				.map(userMapper::userToUserDTO);
	}

	@Override
	@Transactional
	public void upsert(UserInfoEntity userInfo) {
		queryFactory
		.update(userInfoEntity)
		.set(userInfoEntity.name ,userInfo.getName())
		.where(userIdEq(userInfo.getUserId()))
		.execute();
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

	
}
