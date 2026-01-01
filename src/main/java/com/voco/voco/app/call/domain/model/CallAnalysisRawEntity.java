package com.voco.voco.app.call.domain.model;

import java.util.List;
import java.util.Map;

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
@Table(name = "voco_call_analysis_raw")
public class CallAnalysisRawEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "analysis_id", nullable = false)
	private Long analysisId;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "conversation")
	private List<ConversationRaw> conversation;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "task_completion")
	private Map<String, Object> taskCompletion;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "language_accuracy")
	private Map<String, Object> languageAccuracy;

	public record ConversationRaw(
		String role,
		String content
	) {
	}

	public static CallAnalysisRawEntity create(
		Long analysisId,
		List<ConversationRaw> conversation,
		Map<String, Object> taskCompletion,
		Map<String, Object> languageAccuracy
	) {
		CallAnalysisRawEntity entity = new CallAnalysisRawEntity();
		entity.analysisId = analysisId;
		entity.conversation = conversation;
		entity.taskCompletion = taskCompletion;
		entity.languageAccuracy = languageAccuracy;
		return entity;
	}
}
