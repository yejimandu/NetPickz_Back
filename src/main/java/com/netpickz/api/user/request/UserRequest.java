package com.netpickz.api.user.request;

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
public class UserRequest {
	@Schema(description = "사용자 ID", example = "abcde12345")
    private String userId;

	@Schema(description = "사용자 이름", example = "홍길등")
    private String name;

	@Schema(description = "사용자 이메일", example = "hongGuk@naver.com")
	private String email;

	@Schema(description = "사용자 비밀번호", example = "hongGuk!@34532")
	private String password;
	
	private Boolean emailVerified;
	
	private String newPassword;
}
