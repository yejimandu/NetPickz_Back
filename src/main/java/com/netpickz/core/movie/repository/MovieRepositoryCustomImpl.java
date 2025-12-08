package com.netpickz.core.movie.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.entity.MovieInfoEntity;
import com.netpickz.core.movie.entity.QMovieEntity;
import com.netpickz.core.movie.entity.QMovieInfoEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MovieRepositoryCustomImpl implements MovieRepositoryCustom {

	private final JPAQueryFactory queryFactory;


	@Override
	public Optional<MovieDTO> findByMovieId(String movieId) {
		QMovieEntity qMovieEntity = QMovieEntity.movieEntity;
		QMovieInfoEntity qMovieInfoEntity = QMovieInfoEntity.movieInfoEntity;
		
		MovieInfoEntity info = queryFactory
			    .selectFrom(qMovieInfoEntity)
			    .join(qMovieInfoEntity.movieEntity, qMovieEntity).fetchJoin()
			    .where(qMovieEntity.movieId.eq(movieId))
			    .fetchOne();
	 	return Optional.of(MovieDTO.builder()
			    .id(info.getMovieEntity().getId())
			    .movieId(info.getMovieEntity().getMovieId())
			    .title(info.getMovieEntity().getTitle())
			    .overview(info.getOverView())
			    .posterPath(info.getPosterPath())
			    .releaseDate(info.getReleaseDate())
			    .build());
	}


	@Override
	public Optional<MovieDTO> findByExternalId(String id) {
		QMovieEntity qMovieEntity = QMovieEntity.movieEntity;
		QMovieInfoEntity qMovieInfoEntity = QMovieInfoEntity.movieInfoEntity;
		
		MovieInfoEntity info = queryFactory
			    .selectFrom(qMovieInfoEntity)
			    .join(qMovieInfoEntity.movieEntity, qMovieEntity).fetchJoin()
			    .where(qMovieEntity.id.eq(id))
			    .fetchOne();
		if(info == null) {
			return Optional.empty();
		}
	 	return Optional.of(MovieDTO.builder()
			    .id(info.getMovieEntity().getId())
			    .movieId(info.getMovieEntity().getMovieId())
			    .title(info.getMovieEntity().getTitle())
			    .overview(info.getOverView())
			    .posterPath(info.getPosterPath())
			    .releaseDate(info.getReleaseDate())
			    .build());
	}



}
