package com.netpickz.common.jwt;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.netpickz.core.user.entity.UserInfoEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails{
	
	private final UserInfoEntity userInfoEntity; 
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return  Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return userInfoEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return userInfoEntity.getUserId();
	}

}
