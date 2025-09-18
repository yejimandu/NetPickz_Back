package com.netpickz.core.session;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.netpickz.api.login.LoginController;
import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbSessionResponse;

@Service
public class SessionServiceimpl implements SessionService {

	@Autowired
	private TmdbClient tmdbClient;
	
	@Autowired
	private SessionRepository sessionRepository;

	
	@Override
	public SessionDTO createSession(SessionDTO sessionDto) {
		var tmdbSessionResponse =   tmdbClient.createGuestSession().getBody();
		
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        var zdt = ZonedDateTime.parse(tmdbSessionResponse.getExpiresAt(), formatter);
        var timestamp = Timestamp.from(zdt.toInstant());
        var sessionId = "Guest".equals(sessionDto.getSessionType().toString()) ? tmdbSessionResponse.getGuestSessionId() : tmdbSessionResponse.getSessionId();
        var expiresAt =  "Guest".equals(sessionDto.getSessionType().toString()) ? timestamp : null;
		var sessionEntity = SessionEntity.builder()
				.sessionId(sessionId)
				.expiresAt(expiresAt)
				.type(sessionDto.getSessionType())
				.createdAt(Timestamp.from(Instant.now()))
				.build();
        
		sessionRepository.save(sessionEntity);
		
		return sessionDto.toBuilder()
				.sessionId(tmdbSessionResponse.getGuestSessionId())
				.expireDate(tmdbSessionResponse.getExpiresAt())
				.build();
	}

}
