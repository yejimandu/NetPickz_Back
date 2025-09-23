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
@Schema(description = "영화 장르 정보를 담고 있는 클래스")
public class GenreDTO {
	@Schema(description = "장르 아이디", example = "27" , required = true)
	private Integer id;
	@Schema(description = "장르명", example = "공포" , required = true)
	private String name;

}
