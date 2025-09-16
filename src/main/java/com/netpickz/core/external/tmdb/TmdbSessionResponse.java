package com.netpickz.core.external.tmdb;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbSessionResponse {
	
	@JsonProperty("success")
	private boolean success;
	@JsonProperty("guest_session_id")
	private String guestSessionId;
	@JsonProperty("session_id")
	private String sessionId;
	@JsonProperty("expires_at")
	private Timestamp expiresAt;

}
