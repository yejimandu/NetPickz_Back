package com.netpickz.core.movie.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.entity.MovieInfoEntity;
import com.netpickz.core.movie.entity.QMovieEntity;
import com.netpickz.core.movie.entity.QMovieInfoEntity;
import com.netpickz.core.movie.mapper.MovieMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MovieRepositoryCustomImpl implements MovieRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final MovieMapper movieMapper;

	@Override
	public Optional<MovieDTO> findByMovieId(String movieId) {
		return findByPredicate(QMovieEntity.movieEntity.movieId.eq(movieId));
	}

	@Override
	public Optional<MovieDTO> findByExternalId(String id) {
	return findByPredicate(QMovieEntity.movieEntity.id.eq(id));
	}

	private Optional<MovieDTO> findByPredicate(BooleanExpression predicate){
		
		QMovieEntity qMovieEntity = QMovieEntity.movieEntity;
		QMovieInfoEntity qMovieInfoEntity = QMovieInfoEntity.movieInfoEntity;
		MovieInfoEntity info = queryFactory
			    .selectFrom(qMovieInfoEntity)
			    .join(qMovieInfoEntity.movieEntity, qMovieEntity).fetchJoin()
			    .where(predicate)
			    .fetchOne();
		
	 	return Optional.ofNullable(info)
	 				.map(movieMapper::entityToDto);

	}
}
