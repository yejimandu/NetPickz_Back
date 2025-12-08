package com.netpickz.core.mail;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.netpickz.api.mail.request.MailVerifyRequest;
import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.handler.CustomException;

import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

	private final JavaMailSender mailSender;
	private final StringRedisTemplate redisTemplate;
	
	@Override
	public String sendCode(String email) {
		if(email == null || email.isBlank()) {
			throw new CustomException(ErrorCode.MAIL_INVALID);
		}
		var flag = false;
		String code = String.format("%06d", new Random().nextInt(999999));
		
		var sMailMessage  = new SimpleMailMessage();
//		sMailMessage.setFrom("noreply@baeldung.com");
		sMailMessage.setFrom("jeonsongyong27@gmail.com");
		sMailMessage.setTo(email);
		sMailMessage.setSubject("[netpickz] 이메일 인증 코드");
		String msg = "[netpickz] 이메일 인증 코드 \n 아래 인증번호를 입력해주세요\n " + code + "\n (유효시간: 5분)";
		sMailMessage.setText(msg);
		try {
			mailSender.send(sMailMessage);
		}catch (MailException e) {
			throw new CustomException(ErrorCode.MAIL_SEND_FAIL);
		}
		
		try {
			redisTemplate.opsForValue().set(email, code, 5 , TimeUnit.MINUTES );
			flag = true;
		}catch (RedisException e) {
			throw new CustomException(ErrorCode.REDIS_CONNECT_FAIL);
		}
		return flag ? "인증번호가 발송 되었습니다." : "전송 실패";
	}

	@Override
	public String verifyCode(MailVerifyRequest request) {
		var code = request.getCode();
		try {
			var value = redisTemplate.opsForValue().get(request.getEmail());
			return code.equals(value) ? "SUCCESS" : "FAIL";
		}catch (RedisException e) {
			throw new CustomException(ErrorCode.REDIS_CONNECT_FAIL);
		}
	}
	
}
