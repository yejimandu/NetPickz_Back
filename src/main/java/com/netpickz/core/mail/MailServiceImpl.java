package com.netpickz.core.mail;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.netpickz.api.mail.request.MailRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

	@Autowired
	private JavaMailSender mailSender;
	
	private final StringRedisTemplate redisTemplate;
	
	@Override
	public String sendCode(String email) {
		var status = "fail";
		String code = String.format("%06d", new Random().nextInt(999999));
		
		var sMailMessage  = new SimpleMailMessage();
//		sMailMessage.setFrom("noreply@baeldung.com");
		sMailMessage.setFrom("jeonsongyong27@gmail.com");
		sMailMessage.setTo(email);
		sMailMessage.setSubject("[netpickz] 이메일 인증 코드");
		String msg = "인증 번호 " + code + " (유효시간: 5분)";
		sMailMessage.setText(msg);
		try {
			mailSender.send(sMailMessage);
			redisTemplate.opsForValue().set(email, code, 5 , TimeUnit.MINUTES );
			System.out.println("sucess");
			status = "sucess";
		}catch (Exception e) {
			// TODO 로그 저장
			System.out.println(e.getMessage());
		}
		return status;
	}

	@Override
	public String verifyCode(MailRequest request) {
		var value = redisTemplate.opsForValue().get(request.getEmail());
		return request.getCode().equals(value) ? "success" : "fail";
	}

	
	
}
