package com.netpickz.core.auth.repository;

import org.springframework.stereotype.Repository;

import com.netpickz.common.enumType.TokenStatusType;
import com.netpickz.core.auth.entity.QTokenIssuanceHistoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryCustomImpl implements TokenRepositoryCustom{
	
	private final JPAQueryFactory queryFactory;

	@Override
	@Transactional
	public void updateStateByUserId(String userId, TokenStatusType statusType) {
		QTokenIssuanceHistoryEntity qHistoryEntity = QTokenIssuanceHistoryEntity.tokenIssuanceHistoryEntity;
		queryFactory.update(qHistoryEntity)
		.set(qHistoryEntity.status,  statusType)
		.where(qHistoryEntity.userId.eq(userId), qHistoryEntity.status.eq(TokenStatusType.Active))
		.execute();
	}

}
