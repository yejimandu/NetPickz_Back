package com.netpickz.core.user;

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
@Schema(description = "사용자 정보를 담고 있는 클래스")
public class UserDTO {
	@Schema(description = "Tmdb 세션 Id", example = "513b7de4bc99806e25a4b8b8c4a1ec4471ed938f", required = true)
	private String sessionId;
	
	@Schema(description = "사용자 Id", example = "dnkwndks", required = true)
	private String userId;
	
	@Schema(description = "사용자명", example = "홍길동", required = true)
	private String name;
	
	@Schema(description = "이메일", example = "ewfdnis@naver.com", required = true)
	private String email;
	
	private String movieId;
	
	@Schema(description = "상태", example = "정상", required = true)
	private String state;

	private String password;
}
