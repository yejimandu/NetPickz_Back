package com.netpickz.core.auth.repository;

import com.netpickz.common.enumType.TokenStatusType;

public interface TokenRepositoryCustom {

	void updateStateByUserId(String userId, TokenStatusType statusType);

}
