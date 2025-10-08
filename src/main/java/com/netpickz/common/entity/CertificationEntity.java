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
@Table(name="certifications")
@Builder
public class CertificationEntity {
	@Comment("관람등급 아이디")
	@Id
	private String certification ;
	@Comment("관람등급명")
	@Column(length = 500)
	private String meaning ;
	@Comment("순서")
	@Column(name="order_Number")
	private Integer orderNum  ;
}
