package com.netpickz.api.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.netpickz.common.enumType.SessionType;
import com.netpickz.core.session.SessionDTO;
import com.netpickz.core.session.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/session")
@Tag(name = "Session", description = "세션 관련 기능을 제공하는 컨트롤러")
public class SessionController {

	@Autowired
	private SessionService sessionService;
	
	// 1. 간단 구현은 해둠 그치만 보완 필요
	// 2. return 타입 정리 및 session pk 값을 쓸 값 고민  및 값 있는지 없는지 체크 후 있으 면 할당하는 로직 필요.
	@Operation(summary = "게스트 세션 생성 API", description = "간단한 OK 응답 테스트")
	@GetMapping("/guest")
	public ResponseEntity guestSession() throws JsonMappingException, JsonProcessingException {
		SessionDTO sessionDto = SessionDTO.builder() 
	            .sessionType(SessionType.Guest)
//	            .requestToken("test")
	            .build();
		sessionService.createSession(sessionDto);
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	// 1. 간단 구현은 해둠 그치만 보완 필요
	// 2. return 타입 정리 및 session pk 값을 쓸 값 고민  및 값 있는지 없는지 체크 후 있으 면 할당하는 로직 필요.
	@Operation(summary = "사용자 세션 생성 테스트 용 ", description = "간단한 OK 응답 테스트")
	@GetMapping("/user")
	public ResponseEntity userSession() throws JsonMappingException, JsonProcessingException {
		SessionDTO sessionDto = SessionDTO.builder() 
	            .sessionType(SessionType.User)
	            .build();
		sessionService.createSession(sessionDto);
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	
}
