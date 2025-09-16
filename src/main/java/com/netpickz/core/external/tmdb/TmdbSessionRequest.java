package com.netpickz.core.external.tmdb;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TmdbSessionRequest {

	private String requestToken;
	private String sessionId;
}
