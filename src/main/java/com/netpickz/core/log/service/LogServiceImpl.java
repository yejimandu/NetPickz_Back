package com.netpickz.core.log.service;

import org.springframework.stereotype.Service;

import com.netpickz.common.dto.LogDTO;
import com.netpickz.common.repository.SystemLogRepository;
import com.netpickz.core.log.mapper.LogMapper;

import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService{

	private final SystemLogRepository systemLogRepository;
	private final LogMapper logMapper;
    private static final ULID ulidGenerator = new ULID();
	
	@Override
	public void save(LogDTO logDTO) {
		var ulid = ulidGenerator.nextULID();
		var logEntity = logMapper.dtoToEntity(logDTO, ulid);
		systemLogRepository.save(logEntity);
	}

}
