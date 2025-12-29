package com.voco.voco.app.call.domain.model;

import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
	@Column(name = "task_completion", nullable = false)
	private Map<String, Object> taskCompletion;

	@Column(name = "total_user_utterances", nullable = false)
	private Integer totalUserUtterances;

	@Column(name = "correct_utterances", nullable = false)
	private Integer correctUtterances;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "utterance_analyses", nullable = false)
	private List<Map<String, Object>> utteranceAnalyses;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "errors")
	private List<Map<String, Object>> errors;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "error_summary", nullable = false)
	private Map<String, Object> errorSummary;

	@Embedded
	private ScoringEmbeddable scoring;

	@Embedded
	private FeedbackEmbeddable feedback;

	@Column(name = "brief_description", nullable = false, columnDefinition = "TEXT")
	private String briefDescription;

	private CallAnalysisEntity(
		Map<String, Object> taskCompletion,
		Integer totalUserUtterances,
		Integer correctUtterances,
		List<Map<String, Object>> utteranceAnalyses,
		List<Map<String, Object>> errors,
		Map<String, Object> errorSummary,
		ScoringEmbeddable scoring,
		FeedbackEmbeddable feedback,
		String briefDescription
	) {
		this.taskCompletion = taskCompletion;
		this.totalUserUtterances = totalUserUtterances;
		this.correctUtterances = correctUtterances;
		this.utteranceAnalyses = utteranceAnalyses;
		this.errors = errors;
		this.errorSummary = errorSummary;
		this.scoring = scoring;
		this.feedback = feedback;
		this.briefDescription = briefDescription;
	}

	public static CallAnalysisEntity create(
		Map<String, Object> taskCompletion,
		Integer totalUserUtterances,
		Integer correctUtterances,
		List<Map<String, Object>> utteranceAnalyses,
		List<Map<String, Object>> errors,
		Map<String, Object> errorSummary,
		ScoringEmbeddable scoring,
		FeedbackEmbeddable feedback,
		String briefDescription
	) {
		return new CallAnalysisEntity(
			taskCompletion,
			totalUserUtterances,
			correctUtterances,
			utteranceAnalyses,
			errors,
			errorSummary,
			scoring,
			feedback,
			briefDescription
		);
	}
}
