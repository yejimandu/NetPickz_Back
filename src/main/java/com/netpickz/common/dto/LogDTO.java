package com.netpickz.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Schema(description = "로그정보")
public class LogDTO {
	
	private String logId;
	private String serviceName;
	private String path;
	private String method;
	private String userId;
	private String ipAddress;
	private String userAgent;
	private String message;
	private String stackTrace;
	private int statusCode;
}
