package com.netpickz.api.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.mail.request.MailRequest;
import com.netpickz.core.mail.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/mail")
@Tag(name = "Mail", description = "이메일 관련 기능을 제공하는 컨트롤러입니다.")
public class MailController {
	
	@Autowired
	private MailService mailService;
	
	//	TODO 이메일 인증 코드 발송 
	@Operation(summary = "이메일 인증 코드", description = "이메일 인증 코드 발송합니다.")
	@Parameter(name = "email", required = true, description = "인증이 필요한 이메일")
	@GetMapping("/send")
	public ResponseEntity<String> sendEmail(
			@RequestParam(name="email") String email ) {
		// TODO 리턴 추후
		var msg = mailService.sendCode(email);
		return  new ResponseEntity<String>( msg, HttpStatus.OK);
	}

	//	TODO 이메일 인증 코드 확인
	@Operation(summary = "이메일 인증 코드 검증", description = "이메일 인증 코드 검증합니다.")
	@PostMapping("/verify")
	public ResponseEntity<String> verify(
			@org.springframework.web.bind.annotation.RequestBody MailRequest request) {
		// TODO 리턴 추후
		var msg = mailService.verifyCode(request);
		return  new ResponseEntity<String>( msg, HttpStatus.OK);
	}
}
