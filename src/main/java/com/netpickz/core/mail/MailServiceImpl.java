package com.netpickz.core.mail;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.netpickz.api.mail.request.MailVerifyRequest;
import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.constants.Constants;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.common.jwt.JWTUtil;
import com.netpickz.core.user.service.UserService;

import io.lettuce.core.RedisException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

	private final JavaMailSender mailSender;
	private final StringRedisTemplate redisTemplate;
	private final UserService userService;
	private final JWTUtil jwtUtil;
	
	@Value("${spring.mail.username}")
	String mailName;
	
	@Override
	public String sendCode(String email) {
		var msg = Constants.MAIL_SEND_FAIL;
		log.debug("SendCode Email={}" , email);
		if(email == null || email.isBlank()) {
			throw new NetPickzException(ErrorCode.MAIL_INVALID);
		}
		var code = String.format("%06d", new Random().nextInt(999999));
		
		var sMailMessage  = new SimpleMailMessage();
//		sMailMessage.setFrom("noreply@baeldung.com");
		sMailMessage.setFrom(mailName);
		sMailMessage.setTo(email);
		sMailMessage.setSubject(Constants.MAIL_SUBJECT);
		sMailMessage.setText(Constants.MAIL_TEXT1 + code + Constants.MAIL_TEXT2);
		try {
			mailSender.send(sMailMessage);
		}catch (MailException e) {
			log.error("Fail to mail Send. To={}, subject={}, msg={}", sMailMessage.getTo(), sMailMessage.getSubject(), e.getMessage(), e);
			throw new NetPickzException(ErrorCode.MAIL_SEND_FAIL);
		}
		
		try {
			redisTemplate.opsForValue().set(email, code, 5 , TimeUnit.MINUTES );
		}catch (RedisException e) {
			log.error("Fail to Connect Redis. email={}, code={}, msg={}", email, code , e.getMessage(), e);
			throw new NetPickzException(ErrorCode.REDIS_CONNECT_FAIL);
		}
		msg = Constants.MAIL_SEND_SUCCESS;
		return msg;
	}

	@Override
	public String verifyCode(MailVerifyRequest request, String userId) {
		var code = request.getCode();
		var msg = Constants.MAIL_VERIFY_FAIL;
		try {
			// 1. 인증코드 불일치
			var value = redisTemplate.opsForValue().get(request.getEmail());
			if(value == null || !value.equals(code)) {
				throw new NetPickzException(ErrorCode.MAIL_VERIFY_FAIL);
			}
			userService.updateUser(UserRequest.builder().userId(userId).email(request.getEmail()).emailVerified(true).build());
			msg = Constants.MAIL_VERIFY_SUCCESS;
			return msg;
		}catch (RedisException e) {
			log.error("Fail to Connect Redis. email={}, code={}, msg={}", request.getEmail(), code, e.getMessage(), e);
			throw new NetPickzException(ErrorCode.REDIS_CONNECT_FAIL);
		}
	}

	@Override
	public String sendPwChgUrl(String email,  String userId) {
		var msg = Constants.RESET_SEND_FAIL;
		var token = jwtUtil.createJwt(Constants.RESET_TOKEN, userId,  600000L);
		var resetUrl = "http://localhost:5173/user/pwChg?token="+token; // TODO
		
		if(email == null || email.isBlank()) {
			throw new NetPickzException(ErrorCode.MAIL_INVALID);
		}
		
		try {
		    var message = mailSender.createMimeMessage();
		    var helper = new MimeMessageHelper(message, true, "UTF-8");
	
		    helper.setFrom(mailName);
		    helper.setTo(email);
		    helper.setSubject(Constants.MAIL_SUBJECT_PW_CHG);
		    var htmlContent = "<p>비밀번호를 재설정하려면 아래 링크를 클릭하세요.</p>"
		        + "<a href='" + resetUrl + "'>비밀번호 재설정</a>";
	
		    helper.setText(htmlContent, true); // true → HTML 모드
		    mailSender.send(message);
		    
		    redisTemplate.opsForValue().set("pwReset:" + userId, token, 10 , TimeUnit.MINUTES );
		}catch (MailException e) {
//			log.error("Fail to mail Send. To={}, subject={}, msg={}", sMailMessage.getTo(), sMailMessage.getSubject(), e.getMessage(), e);
			throw new NetPickzException(ErrorCode.MAIL_SEND_FAIL);
		}catch (MessagingException e) {
			System.out.println("ddddd");
		}catch (RedisException e) {
			log.error("Fail to Connect Redis. pwReset:  userId={}, token={}, msg={}", userId, token , e.getMessage(), e);
			throw new NetPickzException(ErrorCode.REDIS_CONNECT_FAIL);
		}
		msg = Constants.RESET_SEND_SUCCESS;
		return msg;
	}
	
}
