package com.netpickz.core.movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netpickz.core.external.tmdb.TmdbClient;
import com.netpickz.core.external.tmdb.TmdbMovieRequest;
import com.netpickz.core.external.tmdb.TmdbMovieResponse;
import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.movie.repository.MovieRepository;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private TmdbClient tmdbClient; 
	
	@Override
	public void findByMovieId(String movieId) {
		
		// async 값 기준으로 true 인경우만 tmdb api 호출하고 아닌 경우는 db 조회
		TmdbMovieResponse tmdbMovieResponse = tmdbClient.getMovieInfo(TmdbMovieRequest.builder().movieId(Integer.valueOf(movieId)).build()).getBody();
		MovieEntity movieEntity = MovieEntity.builder().id(tmdbMovieResponse.getId()).title(tmdbMovieResponse.getTitle()).movieId("movie_2rw3r232").build();
		movieRepository.save(movieEntity);
	}

		
}
