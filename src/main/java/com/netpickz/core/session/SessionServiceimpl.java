package com.netpickz.core.session;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.netpickz.api.login.LoginController;
import com.netpickz.common.enumType.SessionType;
import com.netpickz.common.util.IdGenerator;
import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbSessionResponse;
import com.netpickz.core.user.entity.UserEntity;

@Service
public class SessionServiceimpl implements SessionService {


	@Autowired
	private TmdbClient tmdbClient;
	
	@Autowired
	private SessionRepository sessionRepository;

	@Override
	public Optional<SessionDTO> createSession(String userId, SessionType sessionType) {
		var tmdbSessionResponse =   tmdbClient.createGuestSession().getBody();
		
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        var zdt = ZonedDateTime.parse(tmdbSessionResponse.getExpiresAt(), formatter);
        var timestamp = Timestamp.from(zdt.toInstant());
        var sessionId = "Guest".equals(sessionType.toString()) ? tmdbSessionResponse.getGuestSessionId() : tmdbSessionResponse.getSessionId();
        var expiresAt =  "Guest".equals(sessionType.toString()) ? timestamp : null;
		var sessionEntity = SessionEntity.builder()
				.id(IdGenerator.getId("SS_"))
				.sessionId(sessionId)
				.expiresAt(expiresAt)
				.type(sessionType)
				.userEntity(UserEntity.builder().userId(userId).build())
				.build();
        
		var session = sessionRepository.saveAndFlush(sessionEntity);
		
		var sessionDto = SessionDTO.builder()
				.sessionId(session.getSessionId())
				.expireDate(String.valueOf(session.getExpiresAt()))
				.sessionType(session.getType())
				.userId(userId)
				.build();
		return Optional.of(sessionDto);
	}

	@Override
	public Optional<SessionDTO> getSessionInfo(String sessionId) {
		// TODO get 아닌경우
		var sessionEntity = sessionRepository.findBySessionId(sessionId).get();
		var sessionDto = SessionDTO.builder().sessionId(sessionEntity.getSessionId()).userId(sessionEntity.getUserEntity().getUserId()).build();
		return Optional.of(sessionDto);
	}

}
