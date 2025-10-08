package com.netpickz.common.entity;

import org.hibernate.annotations.Comment;

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
	@Column
	private String name;
	@Comment("제공업체로고 이미지경로")
	@Column(name="logo_path")
	private String logoPath;
	@Comment("순서")
	@Column(name="order_number")
	private String orderNum;
}
