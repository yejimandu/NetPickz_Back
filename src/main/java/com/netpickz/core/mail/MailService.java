package com.netpickz.core.mail;

import com.netpickz.api.mail.request.MailVerifyRequest;
import com.netpickz.common.dto.CommonDTO;

public interface MailService {

	CommonDTO sendCode(String email);
	CommonDTO verifyCode(MailVerifyRequest request);
}
