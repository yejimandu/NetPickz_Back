package com.netpickz.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netpickz.common.entity.SystemLogEntity;

public interface SystemLogRepository extends JpaRepository<SystemLogEntity, String>{

}
