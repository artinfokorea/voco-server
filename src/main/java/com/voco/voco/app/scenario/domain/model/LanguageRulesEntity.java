package com.voco.voco.app.scenario.domain.model;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "voco_language_rules")
public class LanguageRulesEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scenario_id", nullable = false)
	private ConversationScenarioEntity scenario;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "vocabulary_rules", nullable = false, columnDefinition = "jsonb")
	private List<String> vocabularyRules;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "sentence_rules", nullable = false, columnDefinition = "jsonb")
	private List<String> sentenceRules;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "output_constraints", nullable = false, columnDefinition = "jsonb")
	private List<String> outputConstraints;

	private LanguageRulesEntity(List<String> vocabularyRules, List<String> sentenceRules,
		List<String> outputConstraints) {
		this.vocabularyRules = vocabularyRules;
		this.sentenceRules = sentenceRules;
		this.outputConstraints = outputConstraints;
	}

	public static LanguageRulesEntity create(List<String> vocabularyRules, List<String> sentenceRules,
		List<String> outputConstraints) {
		return new LanguageRulesEntity(vocabularyRules, sentenceRules, outputConstraints);
	}

	public void setScenario(ConversationScenarioEntity scenario) {
		this.scenario = scenario;
	}
}
