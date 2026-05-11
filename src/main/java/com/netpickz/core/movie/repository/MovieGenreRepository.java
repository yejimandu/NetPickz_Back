package com.netpickz.core.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.movie.entity.MovieGenreEntity;
import com.netpickz.core.movie.entity.pk.MovieGenrePK;

public interface MovieGenreRepository extends JpaRepository<MovieGenreEntity, MovieGenrePK>{

}
