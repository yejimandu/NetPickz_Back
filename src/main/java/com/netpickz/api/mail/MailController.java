package com.netpickz.api.mail;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.mail.request.MailRequest;
import com.netpickz.api.mail.request.MailVerifyRequest;
import com.netpickz.common.dto.CommonDTO;
import com.netpickz.core.mail.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
@Tag(name = "Mail", description = "이메일 관련 기능을 제공하는 컨트롤러입니다.")
public class MailController {
	
	private final MailService mailService;
	
	@Operation(summary = "이메일 인증 코드", description = "이메일 인증 코드 발송합니다.")
	@PostMapping("/send")
	public ResponseEntity<CommonDTO> sendEmail(
			@org.springframework.web.bind.annotation.RequestBody MailRequest request ) {
		var msg = mailService.sendCode(request.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}

	@Operation(summary = "이메일 인증 코드 검증", description = "이메일 인증 코드 검증합니다.")
	@PostMapping("/verify")
	public ResponseEntity<CommonDTO> verify(
			@org.springframework.web.bind.annotation.RequestBody MailVerifyRequest request) {
		var msg = mailService.verifyCode(request);
		return ResponseEntity.status(HttpStatus.OK).body(msg);
	}
}
