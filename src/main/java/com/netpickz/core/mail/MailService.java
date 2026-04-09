package com.netpickz.core.mail;

import com.netpickz.api.mail.request.MailVerifyRequest;

public interface MailService {

	String sendCode(String email);
	String verifyCode(MailVerifyRequest request, String userId);
	String sendPwChgUrl(String email, String userId);
}
