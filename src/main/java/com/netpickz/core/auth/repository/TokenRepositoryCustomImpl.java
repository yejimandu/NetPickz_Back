package com.netpickz.core.auth.repository;

import static com.netpickz.core.auth.entity.QTokenIssuanceHistoryEntity.tokenIssuanceHistoryEntity;

import org.springframework.util.StringUtils;

import com.netpickz.common.enumType.TokenStatusType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenRepositoryCustomImpl implements TokenRepositoryCustom{
	
	private final JPAQueryFactory queryFactory;

	@Override
	@Transactional
	public void updateStateByUserId(String userId, TokenStatusType statusType) {
		
		queryFactory
			.update(tokenIssuanceHistoryEntity)
			.set(tokenIssuanceHistoryEntity.status,  statusType)
			.where(userIdEq(userId), statusEq(TokenStatusType.Active))
		.execute();
	}

	private BooleanExpression userIdEq(String userId) {
		return StringUtils.hasText(userId) ? tokenIssuanceHistoryEntity.userId.eq(userId) : null;
	}
	
	private BooleanExpression statusEq(TokenStatusType statusType) {
		return statusType != null ? tokenIssuanceHistoryEntity.status.eq(statusType) : null;
	}

}


