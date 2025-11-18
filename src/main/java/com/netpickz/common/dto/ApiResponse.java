package com.netpickz.common.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

	private boolean success;
	private int status;
	private String code;
	private String message;
	private LocalDateTime timeStamp;
	private T data;
	
}
