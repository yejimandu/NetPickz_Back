package com.netpickz.core.session.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.common.util.IdGenerator;
import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.session.dto.SessionDTO;
import com.netpickz.core.session.entity.SessionEntity;
import com.netpickz.core.session.mapper.SessionMapper;
import com.netpickz.core.session.repository.SessionRepository;
import com.netpickz.core.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceimpl implements SessionService {

	private final SessionMapper sessionMapper;
	private final TmdbClient tmdbClient;
	private final SessionRepository sessionRepository;
	private final UserRepository userRepository;

	@Override
	public Optional<SessionDTO> createSession(String userId) {
		log.debug("Create Session. userId={}", userId);
		
		var user = userRepository.findById(userId)
	    .orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND));
		
		var tmdbSessionResponse =  tmdbClient.createGuestSession().getBody();
		if(tmdbSessionResponse == null) {
			throw new NetPickzException(ErrorCode.TMDB_SESSION_NOT_FOUND);
		}
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        var zdt = ZonedDateTime.parse(tmdbSessionResponse.getExpiresAt(), formatter);
        var expiresAt = Timestamp.from(zdt.toInstant());
        
        var sessionInfo = sessionRepository.save(SessionEntity.builder()
                .id(IdGenerator.getId("SS_"))
				.sessionId(tmdbSessionResponse.getGuestSessionId())
				.expiresAt(expiresAt)
                .userEntity(user)
				.build());
        
        var sessionDto = sessionMapper.sessionToSessionDTO(sessionInfo);
        log.info("Save Session Info. sessionDto={}", sessionDto.toString());
        return Optional.of(sessionDto);
	}

    @Override
    public Optional<SessionDTO> getSessionInfo(String sessionId) {
    	log.debug("Find Session Info. sessionId={}", sessionId);
        var sessionEntity = sessionRepository.findBySessionId(sessionId)
        		.orElseThrow(() -> new NetPickzException(ErrorCode.SESSION_NOT_FOUND));
        
        var sessionDto = sessionMapper.sessionToSessionDTO(sessionEntity);
        log.info("Session Info Found. sessionId={}, userId={}", sessionDto.getSessionId(), sessionDto.getUserId());
        return Optional.of(sessionDto);
    }

	@Override
	public Optional<SessionDTO> getOrCreateSessionInfo(String userId) {
		log.debug("Find Session Info Or Create Session Info. userId={}", userId);
		
		return sessionRepository.findByUserId(userId)
		.filter(session -> Instant.parse(session.getExpireDate()).isAfter(Instant.now()))
		.or(() -> createSession(userId));
	}
}
