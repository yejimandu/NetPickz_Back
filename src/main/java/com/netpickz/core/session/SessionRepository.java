package com.netpickz.core.session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionEntity, String>{
	
    Optional<SessionEntity> findBySessionId(String sessionId);

}
