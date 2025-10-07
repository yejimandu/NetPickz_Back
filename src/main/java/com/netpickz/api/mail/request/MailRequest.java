package com.netpickz.api.mail.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "이메일 인증 요청 DTO")
@Builder
public class MailRequest {
	@Schema(description = "인증 이메일", example = "hongGuk@naver.com")
    private String email;

	@Schema(description = "인증 코드", example = "1254623")
    private String code;
}
