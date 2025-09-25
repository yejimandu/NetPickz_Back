package com.netpickz.api.movie.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "영화 평점 요청 DTO")
@Builder
public class RatingRequest {
//    @Schema(description = "영화 ID", example = "mv_23312345")
//    private String movieId;

    @Schema(description = "사용자 세션 ID", example = "abcde12345")
    private String sessionId;

	@Schema(description = "사용자가 입력한 평점", example = "5.5")
	private Double value;
}
