package com.netpickz.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name="certifications")
public class CertificationEntity {
	@Id
	private Integer certification ;
	@Column
	private String meaning ;
	@Column(name="order_Number")
	private Integer orderNum  ;
}
