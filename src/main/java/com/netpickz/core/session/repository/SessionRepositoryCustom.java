package com.netpickz.core.session.repository;

import java.util.Optional;

import com.netpickz.core.session.dto.SessionDTO;

public interface SessionRepositoryCustom {
    Optional<SessionDTO> findByUserId(String userId);
}
