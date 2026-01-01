package com.voco.voco.app.call.domain.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;
import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "grade", nullable = false)
	private CallAnalysisGrade grade;

	@Column(name = "overall_score", nullable = false)
	private Integer overallScore;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "conversation")
	private List<ConversationEntry> conversation;

	@Column(name = "task_completion_score")
	private Integer taskCompletionScore;

	@Column(name = "task_completion_summary")
	private String taskCompletionSummary;

	@Column(name = "language_accuracy_score")
	private Integer languageAccuracyScore;

	@Column(name = "language_accuracy_summary")
	private String languageAccuracySummary;

	@Embedded
	private FeedbackEmbeddable feedback;

	public record ConversationEntry(
		String role,
		String content,
		ConversationError error
	) {
	}

	public record ConversationError(
		String errorType,
		String errorSubtype,
		String errorSegment,
		String correction,
		String explanation
	) {
	}

	public static CallAnalysisEntity create(
		List<ConversationEntry> conversation,
		Integer taskCompletionScore,
		String taskCompletionSummary,
		Integer languageAccuracyScore,
		String languageAccuracySummary,
		FeedbackEmbeddable feedback
	) {
		CallAnalysisEntity entity = new CallAnalysisEntity();
		entity.conversation = conversation;
		entity.taskCompletionScore = taskCompletionScore;
		entity.taskCompletionSummary = taskCompletionSummary;
		entity.languageAccuracyScore = languageAccuracyScore;
		entity.languageAccuracySummary = languageAccuracySummary;
		entity.feedback = feedback;

		int totalScore = 0;
		int count = 0;
		if (taskCompletionScore != null) {
			totalScore += taskCompletionScore;
			count++;
		}
		if (languageAccuracyScore != null) {
			totalScore += languageAccuracyScore;
			count++;
		}
		entity.overallScore = count > 0 ? totalScore / count : 0;
		entity.grade = CallAnalysisGrade.fromScore(entity.overallScore);

		return entity;
	}
}
