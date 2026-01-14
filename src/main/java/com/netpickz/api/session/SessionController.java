package com.netpickz.api.session;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.core.session.dto.SessionDTO;
import com.netpickz.core.session.service.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/session")
@Tag(name = "Session", description = "세션 관련 기능을 제공하는 컨트롤러")
public class SessionController {


	private final SessionService sessionService;

    @Operation(summary = "게스트 세션 생성", description = "사용자가 tmdb 기능을 사용하기 위한 게스트 세션 생성")
    @Parameter(name = "userId", required = true, description = "tmdb 게스트 세션을 생성할 사용자 ID")
	@GetMapping("/guest")
    public ResponseEntity<SessionDTO> createGuestSession(
            @RequestParam(name = "userId") String userId) {
        var sessionDto =  sessionService.createSession(userId);
        return ResponseEntity.status(HttpStatus.OK).body(sessionDto.orElse(null));
	}
	
	// 1. 간단 구현은 해둠 그치만 보완 필요
//	// 2. return 타입 정리 및 session pk 값을 쓸 값 고민  및 값 있는지 없는지 체크 후 있으 면 할당하는 로직 필요.
//	@Operation(summary = "사용자 세션 생성", description = "Tmdb 테스트를 위한 테스트용 API")
//	@GetMapping("/user")
//	public ResponseEntity<SessionDTO> userSession() {
//		var sessionDto = sessionService.createSession(SessionDTO.builder() 
//	            .sessionType(SessionType.User)
//	            .build());
//		return  new ResponseEntity<>(sessionDto, HttpStatus.OK);
//	}
	
	
//	@Operation(summary = "세션 생성", description = "Tmdb 테스트를 위한 테스트용 API")
//	@Parameter(name = "type" , description = "세션타입(사용자, 게스트)")
//	@GetMapping("")
//	public ResponseEntity<SessionDTO> userSession2(@RequestParam(name = "type") SessionType type) {
//		var sessionDto = sessionService.createSession(SessionDTO.builder() 
//	            .sessionType(type)
//	            .build());
//		return  new ResponseEntity<>(sessionDto, HttpStatus.OK);
//	}
	
	
}
