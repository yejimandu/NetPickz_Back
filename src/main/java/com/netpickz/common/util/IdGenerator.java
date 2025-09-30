package com.netpickz.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IdGenerator {
		
	public static String getId(String name) {
//		Ms_20250919093000;
		var now = LocalDateTime.now();
		var formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		var id = name + now.format(formatter);
		return id;
	}
}
