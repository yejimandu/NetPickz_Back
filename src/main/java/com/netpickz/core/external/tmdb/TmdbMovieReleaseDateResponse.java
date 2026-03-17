package com.netpickz.core.external.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieReleaseDateResponse {
	
	@JsonProperty("iso_3166_1")
	private String countryCode;
	@JsonProperty("release_dates")
	private List<TmdbReleaseDateResponse> releaseDates;
	
	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmdbReleaseDateResponse {
		
		@JsonProperty("certification")
		private String certification;
		@JsonProperty("release_date")
		private String releaseDate;
		@JsonProperty("type")
		private Integer type;		// 1 Premiere 시사회 , 2 제한적 극장 개봉 (특정 지역/소규모 상영) 3 정식 극장 개봉
									// 4 온라인/스트리밍 서비스 공개 (OTT, VOD 등) 5 DVD, Blu-ray 같은 물리 매체 출시 6 TV
		
	}

}
