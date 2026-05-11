package com.netpickz.core.auth.service;

import com.netpickz.api.auth.request.AccessTokenRequest;
import com.netpickz.api.auth.request.LoginRequest;
import com.netpickz.core.auth.dto.TokenDTO;
import com.netpickz.core.auth.dto.VerifyDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
	TokenDTO userLogin(LoginRequest loginRequest, HttpServletRequest request);
	TokenDTO createToken(String username);
	VerifyDTO verifyToken(AccessTokenRequest tokenRequest, HttpServletRequest request);
	TokenDTO reissueTokens(String refrechToken, HttpServletRequest request);
	String userLogout(AccessTokenRequest tokenRequest, HttpServletRequest request);
}
