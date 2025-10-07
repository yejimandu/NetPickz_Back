package com.netpickz.api.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "로그인 요청 DTO")
@Builder
public class LoginRequest {

    @Schema(description = "사용자 ID", example = "abcde12345")
    private String userId;

	@Schema(description = "사용자 패스워드", example = "adwgs!@3455")
	private String password ;
}
