package com.netpickz.common.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.netpickz.core.user.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailCustomService implements UserDetailsService{

	private final UserInfoRepository userInfoRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) {
		var user = userInfoRepository.findById(username)
				.map(CustomUserDetails :: new )
				.orElseThrow(() -> new UsernameNotFoundException("해당 사용자는 존재하지 않습니다." + username));
		return user;
	}

}
