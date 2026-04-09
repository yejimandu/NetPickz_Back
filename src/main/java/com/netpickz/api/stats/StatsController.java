package com.netpickz.api.stats;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.core.stats.dto.RatingStatsDTO;
import com.netpickz.core.stats.service.StatsSevice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
@Tag(name = "Stats", description = "통계 관련 기능을 제공하는 컨트롤러")
public class StatsController {
	
	private final StatsSevice statsSevice;
	
    @Operation(summary = "마이페이지 년/달/주/일 평가 관련 통계 값 조회", description = "요청 토큰 내 userId 기준으로 마이페이지 기간내 평가 관련 통계 값 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<RatingStatsDTO> getRatingStats(Authentication authentication) {
    	var userId = authentication.getName();
    	var stats = statsSevice.getUserRatingStats(userId);
    	return ResponseEntity.ok(stats.orElse(null));
    }
    
    @Operation(summary = "마이페이지 평가한 작품, 평균 별점 값 등 조회", description = "요청 토큰 내 userId 총 평가한 작품 , 평균 별점 값 등 조회")
    @GetMapping("/profile")
    public ResponseEntity<Object> getUserRatingStats(Authentication authentication) {
    	var userId = authentication.getName();
    	var stats = statsSevice.getUserStats(userId);
    	return ResponseEntity.ok(stats.orElse(null));
    }
}
