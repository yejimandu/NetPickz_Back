package com.netpickz.core.session;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbSessionResponse;

@Service
public class SessionServiceimpl implements SessionService {

	
	@Autowired
	private TmdbClient tmdbClient;
	
	@Autowired
	private SessionRepository sessionRepository;
	
	@Override
	public void createSession(SessionDTO sessionDto) {
		TmdbSessionResponse tmdbSessionResponse =   tmdbClient.createGuestSession().getBody();
		
//		Optional<String> result = Optional.of(text)
//			    .filter(t -> t.contains(keyword));
//		var sessionId = tmdbSessionResponse.getGuestSessionId().? tmdbSessionResponse.getGuestSessionId() : tmdbSessionResponse.getSessionId();
		
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	        ZonedDateTime zdt = ZonedDateTime.parse(tmdbSessionResponse.getExpiresAt(), formatter);
	        Timestamp timestamp = Timestamp.from(zdt.toInstant());

	        System.out.println("변환된 Timestamp: " + timestamp);
	        Timestamp now = Timestamp.from(Instant.now());
		SessionEntity sessionEntity = SessionEntity.builder().sessionId(tmdbSessionResponse.getGuestSessionId()).expiresAt(timestamp)
				.type(sessionDto.getSessionType()).createdAt(now)
				.build();
		System.out.println(sessionEntity);
		sessionRepository.save(sessionEntity);
	}

}
