package com.netpickz.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "사용자 요청 DTO")
@Builder
public class AcessTokenRequest {

	private String accessToken;

}
