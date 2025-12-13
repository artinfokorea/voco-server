package com.voco.voco.app.call.domain.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "voco_call_analysis")
public class CallAnalysisEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "score", nullable = false)
	private Integer score;

	@Column(name = "summary", nullable = false, columnDefinition = "TEXT")
	private String summary;

	private CallAnalysisEntity(String content, Integer score, String summary) {
		this.content = content;
		this.score = score;
		this.summary = summary;
	}

	public static CallAnalysisEntity create(String content, Integer score, String summary) {
		return new CallAnalysisEntity(content, score, summary);
	}
}
