package com.voco.voco.app.scenario.application.usecase.dto.out;

import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

public record ScenarioSummaryInfo(
	Long scenarioId,
	String name,
	String level
) {
	public static ScenarioSummaryInfo from(ConversationScenarioEntity entity) {
		return new ScenarioSummaryInfo(
			entity.getId(),
			entity.getName(),
			entity.getLevel().name()
		);
	}
}
