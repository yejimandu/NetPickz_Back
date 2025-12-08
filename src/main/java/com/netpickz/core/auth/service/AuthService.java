package com.netpickz.core.auth.service;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.core.auth.dto.TokenDTO;

public interface AuthService {
	TokenDTO userLogin(LoginRequest request);
	TokenDTO createToken(String username);
	Boolean verifyToken(AccessTokenRequest request);
	TokenDTO reissueTokens(String refrechToken);
}
