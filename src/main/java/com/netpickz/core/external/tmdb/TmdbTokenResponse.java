package com.netpickz.core.external.tmdb;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbTokenResponse {

	@JsonProperty("success")
	private boolean success;
	@JsonProperty("request_token")
	private String requestToken;
	@JsonProperty("expires_at")
	private Timestamp expiresAt;
}
