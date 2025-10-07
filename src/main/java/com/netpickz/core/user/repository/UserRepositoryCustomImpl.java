package com.netpickz.core.user.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.netpickz.common.enumType.StateType;
import com.netpickz.core.user.UserDTO;
import com.netpickz.core.user.entity.QUserEntity;
import com.netpickz.core.user.entity.QUserInfoEntity;
import com.netpickz.core.user.entity.UserInfoEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<UserDTO> findByUserId(String userId) {
		QUserEntity qUser = QUserEntity.userEntity;
		QUserInfoEntity qUserInfo = QUserInfoEntity.userInfoEntity;
		
		UserInfoEntity userInfo = queryFactory
				.selectFrom(qUserInfo)
				.join(qUserInfo.userEntity, qUser)
				.fetchJoin()
				.where(qUser.userId.eq(userId))
				.fetchOne();
		
		var dto = UserDTO.builder()
				.userId(userInfo.getUserId())
				.name(userInfo.getName())
				.password(userInfo.getPassword())
				.build();
		return Optional.of(dto);
	}

	@Override
	@Transactional
	public void upsert(UserInfoEntity userInfo) {
		QUserInfoEntity qUserInfo = QUserInfoEntity.userInfoEntity;
		
		queryFactory.update(qUserInfo)
		.set(qUserInfo.name ,userInfo.getName())
		.where(qUserInfo.userEntity.userId.eq(userInfo.getUserEntity().getUserId()))
		.execute();
	}

	@Override
	@Transactional
	public void updateStateByUserId(String userId, StateType type) {
		QUserInfoEntity qUserInfo = QUserInfoEntity.userInfoEntity;
		
		queryFactory.update(qUserInfo)
		.set(qUserInfo.state, type)
		.where(qUserInfo.userEntity.userId.eq(userId))
		.execute();
	}
	
	
}
