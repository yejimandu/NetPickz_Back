package com.netpickz.core.movie.mapper;

import org.mapstruct.Mapper;

import com.netpickz.core.movie.dto.MovieDTO;
import com.netpickz.core.movie.entity.MovieInfoEntity;

@Mapper(componentModel = "spring")
public interface MovieMapper {
	MovieDTO movieToMovieDTO(MovieInfoEntity movieInfoEntity);
}
