package com.netpickz.core.log;

import org.springframework.stereotype.Service;

import com.netpickz.common.dto.LogDTO;
import com.netpickz.common.entity.SystemLogEntity;
import com.netpickz.common.repository.SystemLogRepository;

import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService{

	private final SystemLogRepository systemLogRepository;
	
    private static final ULID ulidGenerator = new ULID();
	
	@Override
	public void save(LogDTO logDTO) {
		var ulid = ulidGenerator.nextULID();
		var systemLog = SystemLogEntity.builder()
				.userAgent(logDTO.getUserAgent())
				.ipAddress(logDTO.getIpAddress())
				.logId(ulid)
				.message(logDTO.getMessage())
				.method(logDTO.getMethod())
				.path(logDTO.getPath())
				.serviceName(logDTO.getServiceName())
				.stackTrace(logDTO.getStackTrace())
				.statusCode(logDTO.getStatusCode())
				.userId(logDTO.getUserId())
				.build();
		systemLogRepository.save(systemLog);
	}

}
