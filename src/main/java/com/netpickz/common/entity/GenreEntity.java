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
	@Column(nullable = false)
	private String name;
	
    @Column(name="created_at", nullable = false)
    @UpdateTimestamp
	private Timestamp createdAt;
}
