package com.netpickz.core.movie.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.movie.entity.MovieEntity;

public interface MovieRepository extends JpaRepository<MovieEntity, String>{

	Optional<MovieEntity> findByMovieId(String movieId);

}
