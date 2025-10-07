package com.netpickz.core.auth;

import com.netpickz.api.auth.request.AcessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;

public interface AuthService {

	TokenDTO userLogin(LoginRequest request);
	TokenDTO createToken(String username);
	Boolean verifyToken(AcessTokenRequest request);
	TokenDTO reissue(String accessToken, String refrechToken);
}
