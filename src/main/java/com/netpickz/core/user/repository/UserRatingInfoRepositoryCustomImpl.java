package com.netpickz.core.user.repository;

import static com.netpickz.core.movie.entity.QMovieEntity.movieEntity;
import static com.netpickz.core.movie.entity.QMovieInfoEntity.movieInfoEntity;
import static com.netpickz.core.user.entity.QUserRatingInfoEntity.userRatingInfoEntity;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.stats.dto.RatingStatsDTO;
import com.netpickz.core.stats.dto.UserStatsDTO;
import com.netpickz.core.user.mapper.UserMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRatingInfoRepositoryCustomImpl implements UserRatingInfoRepositoryCustom{

	private final JPAQueryFactory jpaQueryFactory;
	private final UserMapper userMapper;
	
	@Override
	public Page<RatingDTO> findRatingByUserId( String userId, String keyword, Pageable pageable){
		
		var content = jpaQueryFactory
				.select(userRatingInfoEntity)
				.from(userRatingInfoEntity)
				.join(userRatingInfoEntity.movieEntity, movieEntity)
//				.fetchJoin()
				.join(movieEntity.movieInfo, movieInfoEntity)
//				.fetchJoin() // 
				.where(userIdEq(userId), keywordContain(keyword))
				.offset(pageable.getOffset())
	            .limit(pageable.getPageSize());
		
		pageable.getSort().stream().forEach((e) -> {
			var orderSpecifier  = switch (e.getProperty()) {
				case "createdAt" -> e.isDescending() ? 
						userRatingInfoEntity.createdAt.desc():
						userRatingInfoEntity.createdAt.asc();
				case "title" -> e.isDescending() ? 
						userRatingInfoEntity.movieEntity.title.asc():
						userRatingInfoEntity.movieEntity.title.desc() ;
				case "rating" -> e.isDescending() ? 
						userRatingInfoEntity.rating.desc():
						userRatingInfoEntity.rating.asc();
				default -> null;
			};
			if(orderSpecifier != null) {
				content.orderBy(orderSpecifier);
			}
		});
		
		content.fetch();
		
		var countQuery = jpaQueryFactory
				.select(userRatingInfoEntity.count())
				.from(userRatingInfoEntity)
				.join(userRatingInfoEntity.movieEntity, movieEntity)
				.join(movieEntity.movieInfo, movieInfoEntity) 
				.where(userRatingInfoEntity.userEntity.userId.eq(userId));

		return PageableExecutionUtils.getPage(
				content.stream().map(userMapper::entityToDTO).toList(),
				pageable,
				countQuery::fetchOne);
	}
	
	private BooleanExpression userIdEq(String userId) {
		return StringUtils.hasText(userId) ? userRatingInfoEntity.userEntity.userId.eq(userId) : null;
	}

	private BooleanExpression keywordContain(String keyword) {
		return StringUtils.hasText(keyword) ? userRatingInfoEntity.movieEntity.title.contains(keyword) : null;
	}

	@Override
	public Optional<RatingStatsDTO> findRatingCountsByUserId(String userId) {
		int currentWeek = (int) WeekFields.ISO.weekOfYear().getFrom(LocalDate.now());

		var dd = jpaQueryFactory
				.select( new CaseBuilder()
						.when(userRatingInfoEntity.updatedAt.year().eq(LocalDate.now().getYear()))
						.then(1).otherwise(0).sum().coalesce(0).as("yearCount"),
						new CaseBuilder()
						.when(userRatingInfoEntity.updatedAt.month().eq(LocalDate.now().getMonthValue()))
						.then(1).otherwise(0).sum().coalesce(0).as("monthCount"),
						new CaseBuilder()
						.when(userRatingInfoEntity.updatedAt.week().eq(currentWeek))
						.then(1).otherwise(0).sum().coalesce(0).as("weekCount"),
						new CaseBuilder()
						.when(userRatingInfoEntity.updatedAt.dayOfMonth().eq(LocalDate.now().getDayOfMonth()))
						.then(1).otherwise(0).sum().coalesce(0).as("dayCount")
					)
				.from(userRatingInfoEntity)
				.where(userRatingInfoEntity.userEntity.userId.eq(userId))
				.fetchOne();
		
		return Optional.ofNullable( RatingStatsDTO.builder()
					.yearCount(dd.get(0, Integer.class))
					.monthCount(dd.get(1, Integer.class))
					.weekCount(dd.get(2, Integer.class))
					.dayCount(dd.get(3, Integer.class))
				.build());
	}
	
	@Override
	public Optional<UserStatsDTO>  findStatsByUserd(String userId) {
		var result = jpaQueryFactory
				.select(userRatingInfoEntity.count().coalesce(0L).as("totalCount"),
						Expressions.numberTemplate(Double.class, "round({0}, 1)", 
							    userRatingInfoEntity.rating.avg().coalesce(0.0)).as("ratingAvg"))
				.from(userRatingInfoEntity)
				.where(userRatingInfoEntity.userEntity.userId.eq(userId))
				.fetchOne();
		return Optional.ofNullable(UserStatsDTO.builder()
				.totalCount(result.get(0, Long.class))
				.ratingAvg(result.get(1, Double.class))
				.build());
	}

}
