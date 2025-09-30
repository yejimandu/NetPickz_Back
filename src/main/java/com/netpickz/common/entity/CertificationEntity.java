package com.netpickz.common.entity;

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
	@Id
	private String certification ;
	@Column(length = 500)
	private String meaning ;
	@Column(name="order_Number")
	private Integer orderNum  ;
}
