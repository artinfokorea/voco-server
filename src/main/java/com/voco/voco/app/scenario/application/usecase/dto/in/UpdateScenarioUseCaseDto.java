package com.voco.voco.app.scenario.application.usecase.dto.in;

import java.util.List;

import com.voco.voco.app.scenario.domain.model.Level;

public record UpdateScenarioUseCaseDto(
	Long scenarioId,
	String name,
	Level level,
	String aiRole,
	String userRole,
	String completionRule,
	List<String> completionRuleDetail,
	ScenarioContextDto scenarioContext,
	LanguageRulesDto languageRules,
	BehaviorRulesDto behaviorRules,
	List<ConversationStateDto> conversationStates,
	List<ConversationSlotDto> conversationSlots
) {
	public record ScenarioContextDto(
		String context,
		List<String> personality
	) {
	}

	public record LanguageRulesDto(
		List<String> vocabularyRules,
		List<String> sentenceRules,
		List<String> outputConstraints
	) {
	}

	public record BehaviorRulesDto(
		List<String> rules
	) {
	}

	public record ConversationStateDto(
		Integer stateOrder,
		String stateName
	) {
	}

	public record ConversationSlotDto(
		String slotKey,
		List<String> allowedValues
	) {
	}
}
