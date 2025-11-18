package com.netpickz.common.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
	
	public static Cookie createCookie(String key, String value) {
		var cookie = new Cookie(key, value);
		cookie.setMaxAge(24*60*60);
//		cookie.setSecure(true); https 통신을 하는 경우 활성화
//		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}
	
}
