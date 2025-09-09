package com.netpickz.api.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController()
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 관련 기능을 제공하는 컨트롤러")
public class UserController {

	@Operation(summary = "사용자 API", description = "간단한 OK 응답 테스트")
	@GetMapping("/")
	public ResponseEntity<String> user() {
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
}
