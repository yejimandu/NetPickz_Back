package com.netpickz.core.external.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class TmdbWatchProviderResponse {

	@JsonProperty("logo_path")
	private String logoPath;
	@JsonProperty("provider_id")
	private Integer providerId;
	@JsonProperty("provider_name")
	private String providerName;
	@JsonProperty("display_priority")
	private Integer displayPriority;

}
