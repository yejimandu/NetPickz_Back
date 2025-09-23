package com.netpickz.common.dto;

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
@Schema(description = "영화 제공 업체 정보를 담고 있는 클래스")
public class ProviderDTO {

	@Schema(description = "영화 제공 업체 ID", example = "356")
	private Integer id;
	@Schema(description = "영화 제공 업체명", example = "wavve")
	private String name;
	@Schema(description = "영화 제공 업체 로고 이미지 경로", example = "/hPcjSaWfMwEqXaCMu7Fkb529Dkc.jpg")
	private String logoPath;
	@Schema(description = "영화 제공 업체 순서", example = "2")
	private String orderNum;
}
