package com.netpickz.core.stats.dto;

import java.time.LocalDateTime;

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
@Schema(description = "사용자 통계 정보를 담고 있는 클래스")
public class UserStatsDTO {
	private Long totalCount;
	private double ratingAvg;
	private LocalDateTime  createdAt;
}
