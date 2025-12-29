package com.voco.voco.app.scenario.domain.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.voco.voco.common.model.BaseModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "voco_conversation_scenario")
public class ConversationScenarioEntity extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "level", nullable = false, length = 20)
	private Level level;

	@Column(name = "ai_role", nullable = false, length = 50)
	private String aiRole;

	@Column(name = "user_role", nullable = false, length = 50)
	private String userRole;

	@Column(name = "completion_rule", nullable = false, length = 100)
	private String completionRule;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "completion_rule_detail")
	private List<String> completionRuleDetail;

	@OneToOne(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private ScenarioContextEntity scenarioContext;

	@OneToOne(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private LanguageRulesEntity languageRules;

	@OneToOne(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private BehaviorRulesEntity behaviorRules;

	@OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@OrderBy("stateOrder ASC")
	private List<ConversationStateEntity> conversationStates = new ArrayList<>();

	@OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ConversationSlotEntity> conversationSlots = new ArrayList<>();

	private ConversationScenarioEntity(
		String name,
		Level level,
		String aiRole,
		String userRole,
		String completionRule,
		List<String> completionRuleDetail
	) {
		this.name = name;
		this.level = level;
		this.aiRole = aiRole;
		this.userRole = userRole;
		this.completionRule = completionRule;
		this.completionRuleDetail = completionRuleDetail;
	}

	public static ConversationScenarioEntity create(
		String name,
		Level level,
		String aiRole,
		String userRole,
		String completionRule,
		List<String> completionRuleDetail
	) {
		return new ConversationScenarioEntity(
			name,
			level,
			aiRole,
			userRole,
			completionRule,
			completionRuleDetail
		);
	}

	public void addScenarioContext(ScenarioContextEntity context) {
		this.scenarioContext = context;
		context.setScenario(this);
	}

	public void addLanguageRules(LanguageRulesEntity languageRules) {
		this.languageRules = languageRules;
		languageRules.setScenario(this);
	}

	public void addBehaviorRules(BehaviorRulesEntity behaviorRules) {
		this.behaviorRules = behaviorRules;
		behaviorRules.setScenario(this);
	}

	public void addConversationState(ConversationStateEntity state) {
		this.conversationStates.add(state);
		state.setScenario(this);
	}

	public void addConversationSlot(ConversationSlotEntity slot) {
		this.conversationSlots.add(slot);
		slot.setScenario(this);
	}

	public void update(
		String name,
		Level level,
		String aiRole,
		String userRole,
		String completionRule,
		List<String> completionRuleDetail
	) {
		this.name = name;
		this.level = level;
		this.aiRole = aiRole;
		this.userRole = userRole;
		this.completionRule = completionRule;
		this.completionRuleDetail = completionRuleDetail;
	}

	public void updateScenarioContext(String context, List<String> personality) {
		if (this.scenarioContext != null) {
			this.scenarioContext.update(context, personality);
		} else {
			ScenarioContextEntity newContext = ScenarioContextEntity.create(context, personality);
			this.scenarioContext = newContext;
			newContext.setScenario(this);
		}
	}

	public void updateLanguageRules(List<String> vocabularyRules, List<String> sentenceRules,
		List<String> outputConstraints) {
		if (this.languageRules != null) {
			this.languageRules.update(vocabularyRules, sentenceRules, outputConstraints);
		} else {
			LanguageRulesEntity newRules = LanguageRulesEntity.create(vocabularyRules, sentenceRules, outputConstraints);
			this.languageRules = newRules;
			newRules.setScenario(this);
		}
	}

	public void updateBehaviorRules(List<String> rules) {
		if (this.behaviorRules != null) {
			this.behaviorRules.update(rules);
		} else {
			BehaviorRulesEntity newRules = BehaviorRulesEntity.create(rules);
			this.behaviorRules = newRules;
			newRules.setScenario(this);
		}
	}

	public void updateConversationStates(List<ConversationStateEntity> states) {
		this.conversationStates.clear();
		if (states != null) {
			states.forEach(this::addConversationState);
		}
	}

	public void updateConversationSlots(List<ConversationSlotEntity> slots) {
		this.conversationSlots.clear();
		if (slots != null) {
			slots.forEach(this::addConversationSlot);
		}
	}
}
