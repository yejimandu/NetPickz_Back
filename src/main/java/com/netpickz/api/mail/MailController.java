package com.netpickz.api.mail;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.mail.request.MailRequest;
import com.netpickz.api.mail.request.MailVerifyRequest;
import com.netpickz.core.mail.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
	public ResponseEntity<String> sendEmail(
			@org.springframework.web.bind.annotation.RequestBody MailRequest mailRequest, HttpServletRequest request ) {
//		request.setAttribute("userId", username);
		var msg = mailService.sendCode(mailRequest.getEmail());
		return ResponseEntity.ok(msg);
	}

	@Operation(summary = "이메일 인증 코드 검증", description = "이메일 인증 코드 검증합니다.")
	@PostMapping("/verify")
	public ResponseEntity<String> verify(
			@org.springframework.web.bind.annotation.RequestBody MailVerifyRequest request, Authentication authentication) {
		var userId = authentication.getName();
		var msg = mailService.verifyCode(request, userId);
		return ResponseEntity.ok(msg);
	}
	
	@Operation(summary = "비밀번호 재설정 링크 발송", description = "비밀번호 재설정 링크를 발송합니다.")
	@PostMapping("/pw/send")
	public ResponseEntity<String> pwChgSendEmail(
			@org.springframework.web.bind.annotation.RequestBody MailRequest request, Authentication authentication) {
		var userId = authentication.getName();
		var msg = mailService.sendPwChgUrl(request.getEmail(), userId);
		return ResponseEntity.ok(msg);
	}
	
}
