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
@Table(name="providers")
@Builder
public class ProvidersEntity {
	@Id
	private String id;
	@Column
	private String name;
	@Column(name="logo_path")
	private String logoPath;
	@Column(name="order_number")
	private String orderNum;
}
