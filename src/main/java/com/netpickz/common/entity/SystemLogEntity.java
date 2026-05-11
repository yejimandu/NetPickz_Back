package com.netpickz.common.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="system_log")
@Builder
public class SystemLogEntity {

	@Id
	@Column(name="log_id")
	private String logId;
	
	@Column(name="service_name", nullable = false)
	private String serviceName;
	
	@Column(nullable = false)
	private String path;
	
	@Column(nullable = false)
	private String method;
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="ip_address")
	private String ipAddress;
	
	@Column(name="user_agent")
	private String userAgent;
	
	@Column
	private String message;
	
	@Column(columnDefinition = "TEXT") // DB에 긴 문자열 저장
	private String stackTrace;
	
	@Column
	private int statusCode;
	
	@Column(name="created_at", nullable = false)
	@UpdateTimestamp
	private Timestamp createdAt;
}
