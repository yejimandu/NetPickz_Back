package com.netpickz.core.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.core.movie.entity.MovieProviderEntity;
import com.netpickz.core.movie.entity.pk.MovieProviderPK;

public interface MovieProviderRepository extends JpaRepository<MovieProviderEntity, MovieProviderPK>{

}
