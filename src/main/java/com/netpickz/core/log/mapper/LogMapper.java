package com.netpickz.core.log.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.netpickz.common.dto.LogDTO;
import com.netpickz.common.entity.SystemLogEntity;

@Mapper(componentModel = "spring")
public interface LogMapper {
	
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(source = "ulid", target = "logId")
	SystemLogEntity dtoToEntity(LogDTO logDTO, String ulid);
	
}
