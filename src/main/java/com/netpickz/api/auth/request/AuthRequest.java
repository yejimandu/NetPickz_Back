package com.netpickz.api.auth.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
//@Schema(description = "사용자 요청 DTO")
@Builder
public class AuthRequest {
	private String accessToken;
	private String refrechToken;
}
