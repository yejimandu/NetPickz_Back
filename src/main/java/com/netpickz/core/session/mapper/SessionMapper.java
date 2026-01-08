package com.netpickz.core.session.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.netpickz.core.session.dto.SessionDTO;
import com.netpickz.core.session.entity.SessionEntity;

@Mapper(componentModel = "spring")
public interface SessionMapper {
	
	// TODO 
	@Mapping(source = "type",  target = "sessionType")
	@Mapping(source = "userEntity.userId",  target = "userId")
	@Mapping(source = "expiresAt", target = "expireDate")
	SessionDTO sessionToSessionDTO(SessionEntity sessionEntity);
}
