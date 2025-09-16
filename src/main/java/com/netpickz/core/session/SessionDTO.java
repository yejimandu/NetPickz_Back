package com.netpickz.core.session;

import java.sql.Timestamp;

import com.netpickz.common.enumType.SessionType;

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
public class SessionDTO {

	private SessionType sessionType;
	private String requestToken;
	
	private String sessionId;
	private Timestamp expireDate;
}
