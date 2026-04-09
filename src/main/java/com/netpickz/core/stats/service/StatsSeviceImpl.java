package com.netpickz.core.stats.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.netpickz.core.stats.dto.RatingStatsDTO;
import com.netpickz.core.stats.dto.UserStatsDTO;
import com.netpickz.core.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsSeviceImpl implements StatsSevice {
	
	private final UserService userService;
	
	@Override
	public Optional<RatingStatsDTO> getUserRatingStats(String userId) {
		log.debug("Find Rating Stats: userId={}" , userId);
		// userId 기준으로 평가한 이번년도, 이번달, 이번주 값 구하기 
		var ratingStats = userService.getUserRatingCounts(userId);
		log.debug("Find Rating Stats: WeekCount={}, MonthCount={}, WeekCount={}, DayCount={}" , 
				ratingStats.get().getWeekCount(), ratingStats.get().getMonthCount(),
				ratingStats.get().getWeekCount(), ratingStats.get().getDayCount());
		return ratingStats;
	}

	@Override
	public Optional<UserStatsDTO> getUserStats(String userId) {
		log.debug("Find Users Stats: userId={}" , userId);
		var userDto = userService.getUserInfoByUserId(userId);
		var result = userService.getUserStats(userId);
		var usersStats  = result.get();
		if(result.isPresent() && userDto.isPresent()) {
			var updatedStats = UserStatsDTO.builder()
				.ratingAvg(usersStats.getRatingAvg())
				.totalCount(usersStats.getTotalCount())
				.createdAt(userDto.get().getCreatedAt().toLocalDateTime())
				.build();
			log.debug("Find Users Stats: ratingAvg={}, totalCount={}, createdAt={}" ,  
					updatedStats.getRatingAvg(), updatedStats.getTotalCount(), updatedStats.getCreatedAt());
			return Optional.of(updatedStats);
		}
		return Optional.empty();
	}

	
}
