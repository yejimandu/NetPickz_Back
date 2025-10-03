package com.netpickz.core.session;

import com.netpickz.common.enumType.SessionType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Schema(description = "Tmdb 세션 관련 스키마, 테스트용도")
public class SessionDTO {
	
	@Schema(description = "세션 타입", example = "['guest','user']", required = true )
	private SessionType sessionType;
//	@Schema(description = "요청 토큰(세션 타입이 유저인 경우)", example = "ff5c7eeb5a8870efe3cd7fc5c282cffd26800ecd" )
//	private String requestToken;	
	@Schema(description = "세션 아이디", example = "79191836ddaa0da3df76a5ffef6f07ad6ab0c641", required = true )
	private String sessionId;
	@Schema(description = "세션 만료일(세션 타입이 게스트인 경우)", example = "2025-09-14 05:53:54 UTC")
	private String expireDate;
    @Schema(description = "사용자 아이디", example = "hongig32", required = true )
    private String userId;
}
