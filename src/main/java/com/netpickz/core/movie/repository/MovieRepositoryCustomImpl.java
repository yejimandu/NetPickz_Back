package com.netpickz.core.movie.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.netpickz.core.movie.MovieDTO;
import com.netpickz.core.movie.entity.MovieEntity;
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

			// 이제 안전하게 접근 가능
			System.out.println(info.getMovieEntity().getTitle());

		
//	 	MovieInfoEntity info = (MovieInfoEntity) queryFactory
//	 			.selectFrom(qMovieInfoEntity)
//	 			.innerJoin(qMovieInfoEntity.movieEntity, qMovieEntity)
//	 			.fetchJoin()
//	 		    .where(qMovieEntity.movieId.eq(movieId))
//	 			.fetchOne();
//	 	System.out.println(info);
		var dto = MovieDTO.builder()
		    .id(info.getMovieEntity().getId())
		    .movieId(info.getMovieEntity().getMovieId())
		    .title(info.getMovieEntity().getTitle())
		    .overview(info.getOverView())
		    .posterPath(info.getPosterPath())
		    .releaseDate(info.getReleaseDate())
		    .build();

	 	return Optional.of(dto);
	}



}
