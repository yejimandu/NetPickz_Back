package com.netpickz.core.movie.repository;

import static com.netpickz.core.movie.entity.QMovieEntity.movieEntity;
import static com.netpickz.core.movie.entity.QMovieInfoEntity.movieInfoEntity;

import java.util.Optional;

import org.springframework.util.StringUtils;

import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.entity.MovieInfoEntity;
import com.netpickz.core.movie.mapper.MovieMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MovieRepositoryCustomImpl implements MovieRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final MovieMapper movieMapper;

	@Override
	public Optional<MovieDTO> findByMovieId(String movieId) {
		
		MovieInfoEntity info = queryFactory
			    .selectFrom(movieInfoEntity)
			    .join(movieInfoEntity.movieEntity, movieEntity).fetchJoin()
			    .where(movieIdEq(movieId))
			    .fetchOne();
		
		return Optional.ofNullable(info)
			.map(movieMapper::entityToDto);
	}

	@Override
	public Optional<MovieDTO> findByExternalId(String id) {
		
		MovieInfoEntity info = queryFactory
			    .selectFrom(movieInfoEntity)
			    .join(movieInfoEntity.movieEntity, movieEntity).fetchJoin()
			    .where(idEq(id))
			    .fetchOne();
		
		return Optional.ofNullable(info)
			.map(movieMapper::entityToDto);
	}
	
	private BooleanExpression idEq(String id) {
		return StringUtils.hasText(id) ? movieEntity.id.eq(id) : null;
	}

	private BooleanExpression movieIdEq(String movieId) {
		return StringUtils.hasText(movieId) ? movieEntity.movieId.eq(movieId) : null;
	}

}
