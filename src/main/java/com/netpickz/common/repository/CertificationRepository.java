package com.netpickz.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.common.entity.CertificationEntity;
import com.netpickz.core.movie.entity.MovieEntity;

public interface CertificationRepository extends JpaRepository<CertificationEntity, Integer>{

}
