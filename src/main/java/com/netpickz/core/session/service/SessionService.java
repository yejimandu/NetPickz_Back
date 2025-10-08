package com.netpickz.core.session.service;

import java.util.Optional;

import com.netpickz.common.enumType.SessionType;
import com.netpickz.core.session.dto.SessionDTO;

public interface SessionService {

    Optional<SessionDTO> createSession(String userId, SessionType sessionType); // user , guest 세션 생성
	Optional<SessionDTO> getSessionInfo(String sessionId);
}
