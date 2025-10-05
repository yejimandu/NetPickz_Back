package com.netpickz.core.mail;

import com.netpickz.api.mail.request.MailRequest;

public interface MailService {

	String sendCode(String email);
	String verifyCode(MailRequest request);

}
