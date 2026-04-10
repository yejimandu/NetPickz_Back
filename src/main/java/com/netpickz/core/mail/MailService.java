package com.netpickz.core.mail;

import com.netpickz.api.mail.request.MailVerifyRequest;
import com.netpickz.core.user.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface MailService {

	String sendCode(String email);
	String verifyCode(MailVerifyRequest request, String userId);
	String sendPwChgUrl(UserDTO userDTO, HttpServletRequest request);
}
