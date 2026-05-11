package com.netpickz.common.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UpdateTimestamp;

import com.netpickz.core.movie.entity.MovieInfoEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	@Id@Column(unique = true , name = "certification_id")
	private String certificationId ;
	
	@Comment("관람등급 설명")
	@Column(length = 500)
	private String meaning ;
	
	@Comment("순서")
	@Column(name="order_Number")
	private Integer orderNum  ;
	
	@Column(name="created_at", nullable = false)
    @UpdateTimestamp
	private Timestamp createdAt;
	
	
	@OneToMany(mappedBy = "certificationEntity", cascade = CascadeType.ALL, orphanRemoval = true
			,fetch = FetchType.LAZY)
	private List<MovieInfoEntity> movies = new ArrayList();
	
	
}
