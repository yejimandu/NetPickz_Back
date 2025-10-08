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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="genres")
@Builder
public class GenreEntity {

	@Comment("장르 아이디")
	@Id
	private String id;
	@Comment("장르명")
	@Column
	private String name;
}
