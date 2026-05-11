package com.netpickz.core.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.auth.entity.TokenIssuanceHistoryEntity;

public interface TokenIssuanceHistoryRepository extends JpaRepository<TokenIssuanceHistoryEntity, String>, TokenRepositoryCustom{

}
