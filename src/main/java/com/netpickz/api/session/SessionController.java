package com.netpickz.api.session;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.common.constants.Constants;
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
            @RequestParam(name = "userId") String userId,  Authentication authentication) {
    	if(Constants.ME.equals(userId)) {
			userId = authentication.getName();
		}
        var sessionDto =  sessionService.createSession(userId);
        return ResponseEntity.status(HttpStatus.OK).body(sessionDto.orElse(null));
	}

    @Operation(summary = "세션 아이디 조회", description = "userId 기준으로 tmdb 세션 아이디를 조회합니다. 존재하지 않거나 만료된 경우 재발급하여 조회.")
    @Parameter(name = "userId", required = true, description = "tmdb 게스트 세션을 생성할 사용자 ID")
    @GetMapping("")
    public ResponseEntity<SessionDTO> getSession(
    		@RequestParam(name = "userId") String userId, Authentication authentication) {
    	if(Constants.ME.equals(userId)) {
    		userId = authentication.getName();
    	} 
    	var sessionDto =  sessionService.getOrCreateSessionInfo(userId);
    	return ResponseEntity.ok(sessionDto.orElse(null));
    }

}
