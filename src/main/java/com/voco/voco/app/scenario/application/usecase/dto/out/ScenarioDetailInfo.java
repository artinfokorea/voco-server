package com.voco.voco.app.scenario.application.usecase.dto.out;

import java.util.List;
import java.util.stream.Collectors;

import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

public record ScenarioDetailInfo(
	Long scenarioId,
	String name,
	String level,
	String aiRoleEn,
	String aiRoleKo,
	String userRoleEn,
	String userRoleKo,
	String completionRule,
	List<String> completionRuleDetail,
	ScenarioContextInfo scenarioContext,
	LanguageRulesInfo languageRules,
	BehaviorRulesInfo behaviorRules,
	List<ConversationStateInfo> conversationStates,
	List<ConversationSlotInfo> conversationSlots
) {
	public record ScenarioContextInfo(
		String context,
		List<String> personality
	) {
	}

	public record LanguageRulesInfo(
		List<String> vocabularyRules,
		List<String> sentenceRules,
		List<String> outputConstraints
	) {
	}

	public record BehaviorRulesInfo(
		List<String> rules
	) {
	}

	public record ConversationStateInfo(
		Integer stateOrder,
		String stateName
	) {
	}

	public record ConversationSlotInfo(
		String slotKey,
		List<String> allowedValues
	) {
	}

	public static ScenarioDetailInfo from(ConversationScenarioEntity entity) {
		return new ScenarioDetailInfo(
			entity.getId(),
			entity.getName(),
			entity.getLevel().name(),
			entity.getAiRoleEn(),
			entity.getAiRoleKo(),
			entity.getUserRoleEn(),
			entity.getUserRoleKo(),
			entity.getCompletionRule(),
			entity.getCompletionRuleDetail(),
			entity.getScenarioContext() != null
				? new ScenarioContextInfo(
					entity.getScenarioContext().getContext(),
					entity.getScenarioContext().getPersonality()
				)
				: null,
			entity.getLanguageRules() != null
				? new LanguageRulesInfo(
					entity.getLanguageRules().getVocabularyRules(),
					entity.getLanguageRules().getSentenceRules(),
					entity.getLanguageRules().getOutputConstraints()
				)
				: null,
			entity.getBehaviorRules() != null
				? new BehaviorRulesInfo(entity.getBehaviorRules().getRules())
				: null,
			entity.getConversationStates() != null
				? entity.getConversationStates().stream()
					.map(s -> new ConversationStateInfo(s.getStateOrder(), s.getStateName()))
					.collect(Collectors.toList())
				: null,
			entity.getConversationSlots() != null
				? entity.getConversationSlots().stream()
					.map(s -> new ConversationSlotInfo(s.getSlotKey(), s.getAllowedValues()))
					.collect(Collectors.toList())
				: null
		);
	}
}
