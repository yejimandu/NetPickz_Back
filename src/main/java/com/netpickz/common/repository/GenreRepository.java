package com.netpickz.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.common.entity.GenreEntity;

public interface GenreRepository extends JpaRepository<GenreEntity, String>{

}
