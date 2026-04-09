package com.netpickz.core.session.repository;

import static com.netpickz.core.session.entity.QSessionEntity.sessionEntity;

import java.util.Optional;

import com.netpickz.core.session.dto.SessionDTO;
import com.netpickz.core.session.mapper.SessionMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionRepositoryCustomImpl implements SessionRepositoryCustom{
	
	private final JPAQueryFactory queryFactory;
	private final SessionMapper sessionMapper;
	
	@Override
	public Optional<SessionDTO> findByUserId(String userId) {
		var sessionInfo = queryFactory.selectFrom(sessionEntity)
					.where(sessionEntity.userEntity.userId.eq(userId))
					.orderBy(sessionEntity.expiresAt.desc())
					.limit(1)
					.fetchOne();
		return Optional.ofNullable(sessionInfo)
				.map(sessionMapper::sessionToSessionDTO);
	}

}
