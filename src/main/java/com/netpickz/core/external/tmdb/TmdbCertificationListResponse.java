package com.netpickz.core.external.tmdb;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbCertificationListResponse {
	@JsonProperty("certifications")
	private Map<String, List<TmdbCertificationResponse>> certifications;
}
