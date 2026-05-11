package com.netpickz.common.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.Comment;
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
@Table(name="providers")
@Builder
public class ProvidersEntity {
	@Comment("제공업체 아이디")
	@Id
	private String id;
	
	@Comment("제공업체명")
	@Column(nullable = false)
	private String name;
	
	@Comment("제공업체로고 이미지경로")
	@Column(name="logo_path", nullable = false)
	private String logoPath;
	
	@Comment("순서")
	@Column(name="order_number")
	private Integer orderNum;

	@Column(name="created_at", nullable = false)
    @UpdateTimestamp
	private Timestamp createdAt;
}
