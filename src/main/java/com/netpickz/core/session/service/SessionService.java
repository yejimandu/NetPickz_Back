package com.netpickz.core.session.service;

import java.util.Optional;

import com.netpickz.core.session.dto.SessionDTO;

public interface SessionService {

    Optional<SessionDTO> createSession(String userId);
    Optional<SessionDTO> getSessionInfo(String sessionId);
	Optional<SessionDTO> getSessionInfo2(String userId);
}
