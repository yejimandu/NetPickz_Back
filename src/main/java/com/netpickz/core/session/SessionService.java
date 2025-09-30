package com.netpickz.core.session;

import java.util.Optional;

import com.netpickz.common.enumType.SessionType;

public interface SessionService {

	Optional<SessionDTO> createSession(String userId, SessionType sessionType); // user , guest 세션 생성

	Optional<SessionDTO> getSessionInfo(String sessionId);

}
