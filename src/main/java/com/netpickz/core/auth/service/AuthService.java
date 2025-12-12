package com.netpickz.core.auth.service;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.dto.VerifyDTO;

public interface AuthService {
	TokenDTO userLogin(LoginRequest request);
	TokenDTO createToken(String username);
	VerifyDTO verifyToken(AccessTokenRequest request);
	TokenDTO reissueTokens(String refrechToken);
	String userLogout(AccessTokenRequest request);
}
