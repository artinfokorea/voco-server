package com.voco.voco.app.call.application.usecase.dto.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.ConversationSlotEntity;
import com.voco.voco.app.scenario.domain.model.ConversationStateEntity;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ScenarioMetadata(
	Long callId,
	ScenarioSpec scenarioSpec
) {
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record ScenarioSpec(
		Long scenarioId,
		String scenarioTitle,
		String language,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		String aiRoleEn,
		String aiRoleKo,
		String userRoleEn,
		String userRoleKo,
		String level,
		String scenarioContext,
		String personality,
		String vocabRules,
		String sentenceRules,
		String outputConstraints,
		String behaviorRules,
		List<String> stateList,
		List<SlotInfo> slots,
		String completionRule
	) {
	}

	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public record SlotInfo(
		String name,
		String description
	) {
	}

	public static ScenarioMetadata from(ConversationScenarioEntity scenario, Long callId) {
		ScenarioSpec spec = new ScenarioSpec(
			scenario.getId(),
			scenario.getName(),
			"en",
			scenario.getCreatedAt(),
			scenario.getUpdatedAt(),
			scenario.getAiRoleEn(),
			scenario.getAiRoleKo(),
			scenario.getUserRoleEn(),
			scenario.getUserRoleKo(),
			scenario.getLevel().name(),
			scenario.getScenarioContext() != null ? scenario.getScenarioContext().getContext() : null,
			formatList(scenario.getScenarioContext() != null ? scenario.getScenarioContext().getPersonality() : null),
			formatList(scenario.getLanguageRules() != null ? scenario.getLanguageRules().getVocabularyRules() : null),
			formatList(scenario.getLanguageRules() != null ? scenario.getLanguageRules().getSentenceRules() : null),
			formatList(scenario.getLanguageRules() != null ? scenario.getLanguageRules().getOutputConstraints() : null),
			formatList(scenario.getBehaviorRules() != null ? scenario.getBehaviorRules().getRules() : null),
			scenario.getConversationStates().stream()
				.map(ConversationStateEntity::getStateName)
				.collect(Collectors.toList()),
			scenario.getConversationSlots().stream()
				.map(slot -> new SlotInfo(slot.getSlotKey(), formatSlotValues(slot)))
				.collect(Collectors.toList()),
			formatCompletionRule(scenario)
		);

		return new ScenarioMetadata(callId, spec);
	}

	private static String formatList(List<String> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.stream()
			.map(item -> "- " + item)
			.collect(Collectors.joining("\n"));
	}

	private static String formatSlotValues(ConversationSlotEntity slot) {
		if (slot.getAllowedValues() == null || slot.getAllowedValues().isEmpty()) {
			return "";
		}
		return String.join(" / ", slot.getAllowedValues());
	}

	private static String formatCompletionRule(ConversationScenarioEntity scenario) {
		StringBuilder sb = new StringBuilder(scenario.getCompletionRule());
		if (scenario.getCompletionRuleDetail() != null && !scenario.getCompletionRuleDetail().isEmpty()) {
			sb.append(":\n");
			for (String detail : scenario.getCompletionRuleDetail()) {
				sb.append("- ").append(detail).append("\n");
			}
		}
		return sb.toString().trim();
	}
}
