package com.netpickz.core.stats.dto;


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
@Schema(description = "평점 통계 정보를 담고 있는 클래스")
public class RatingStatsDTO {

	private int yearCount;
	private int monthCount;
	private int weekCount;
	private int dayCount;
}
