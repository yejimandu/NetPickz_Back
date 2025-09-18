package com.netpickz.api.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.core.login.LoginService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/login")
@Tag(name = "Login", description = "로그인 관련 기능을 제공하는 컨트롤러")
public class LoginController {

	@Autowired
	@Lazy
	private LoginService loginService;
	
	@Operation(summary = "게스트 입장 처리", description = "게스트 로그인 처리 ")
	@GetMapping("/guest")
	public ResponseEntity guestLogin()  {
		// TOTO
		loginService.guestLogin();
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	
	@Operation(summary = "사용자 로그인 처리 ", description = "사용자 로그인 처리")
	@GetMapping("/user")
	public ResponseEntity<String> userLogin()  {
		// TOTO
		
		return  new ResponseEntity<>("OK", HttpStatus.OK);
	}
}
