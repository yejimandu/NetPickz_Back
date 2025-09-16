package com.netpickz.core.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.movie.entity.MovieEntity;
import com.netpickz.core.movie.entity.MovieGenreEntity;

public interface MovieGenreRepository extends JpaRepository<MovieGenreEntity, Integer>{

}
