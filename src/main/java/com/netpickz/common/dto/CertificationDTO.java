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
@Schema(description = "영화 관람등급 정보를 담고 있는 클래스")
public class CertificationDTO {
	@Schema(description = "등급 코드", example = "All" , required = true)
	private String certification;
	@Schema(description = "등급 설명", example = "Film suitable for all ages." , required = true)
	private String meaning;
	@Schema(description = "등급 순서", example = "0" , required = true)
	private Integer order;
}
