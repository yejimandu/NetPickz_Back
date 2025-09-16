package com.netpickz.core.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netpickz.common.enumType.SessionType;
import com.netpickz.core.session.SessionDTO;
import com.netpickz.core.session.SessionService;

@Service
public class LoginServiceImpl implements LoginService{

	@Autowired
	private SessionService sessionService;
	
	@Override
	public void guestLogin() {
		sessionService.createSession(SessionDTO.builder().sessionType(SessionType.Guest).build());
		
		
	}

}
