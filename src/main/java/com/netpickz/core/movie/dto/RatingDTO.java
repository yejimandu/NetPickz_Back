package com.netpickz.core.movie.dto;

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
@Schema(description = "영화 평가 정보를 담고 있는 클래스")
public class RatingDTO {
	@Schema(description = "사용자 ID", example = "hongglid123" , required = true)
	private String userId;
	@Schema(description = "영화 ID", example = "MV_20251003150116" , required = true)
	private String movieId;
	@Schema(description = "평점(0.5점 단위)", example = "5.5" , required = true)
	private Double rating;
	@Schema(description = "사용자의 세션아이디", example = "c482d79f1152ade383fe3c2a65d31cbd" , required = true)
	private String sessionId;

}
