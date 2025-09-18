package com.netpickz.core.movie;

import java.util.List;

import com.netpickz.core.external.tmdb.TmdbGenreResponse;

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
@Schema(description = "영화 정보를 담고 있는 클래스")
public class MovieDTO {
	
	@Schema(description = "영화 포스터 이미지 경로", example = "/y9jKDJuf5WCDjSPsqkt1cb0JHGm.jpg" , required = true)
	private String posterPath;
	@Schema(description = "영화 Id", example = "mv_32748372823", required = true)
	private String movieId;
	@Schema(description = "Tmdb 영화 고유 Id", example = "1038392", required = true)
	private Integer id;
	@Schema(description = "영화 개봉일", example = "2025-09-03", required = true)
	private String releaseDate;
	@Schema(description = "영화 개요", example = "1986년 펜실베니아,…", required = true)
	private String overview;
	@Schema(description = "상영 시간(분)", example = "136", required = true)
	private Integer runtime;
	@Schema(description = "영화 제목", example = "컨저링: 마지막 의식", required = true)
	private String title;
	private List<TmdbGenreResponse> genres;
	private String originalLanguage;
	private String status;
}
