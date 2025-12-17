package com.netpickz.common.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.netpickz.common.enumType.ErrorCode;
import com.netpickz.common.handler.NetPickzException;
import com.netpickz.core.user.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailCustomService implements UserDetailsService{

	private final UserInfoRepository userInfoRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		return userInfoRepository.findById(username)
				.map(CustomUserDetails :: new )
				.orElseThrow(() -> new NetPickzException(ErrorCode.USER_NOT_FOUND));
	}

}
