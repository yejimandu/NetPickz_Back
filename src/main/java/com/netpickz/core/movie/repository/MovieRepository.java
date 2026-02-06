package com.netpickz.core.movie.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.movie.entity.MovieEntity;

public interface MovieRepository extends JpaRepository<MovieEntity, String>, MovieRepositoryCustom{

}
