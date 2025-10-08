package com.netpickz.core.session.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.session.entity.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, String>{
	
    Optional<SessionEntity> findBySessionId(String sessionId);

}
