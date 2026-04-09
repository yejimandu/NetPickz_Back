package com.netpickz.core.movie.dto;

import java.util.List;

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
@Schema(description = "결과 리스트 정보와 페이지 정보를 담고 있는 클래스")
public class PageDTO<T> {

	@Schema(description = "총 검색 결과", example = "13", required = true)
    private int totalCount;
    @Schema(description = "총 페이지", example = "13", required = true)
    private int totalPage;
	    
    private List<T> result;
}
