package com.netpickz.common.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import io.swagger.v3.oas.models.PathItem.HttpMethod;
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
	@Column(name="created_at")
	@UpdateTimestamp
	private Timestamp createdAt;
	@Column(name="service_name")
	private String serviceName;
	@Column
	private String path;
	@Column
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
}
