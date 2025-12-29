package com.voco.voco.app.scenario.presentation.controller.dto.out;

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioSummaryInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "시나리오 요약 응답")
public record ScenarioSummaryResponse(
	@Schema(description = "시나리오 ID", example = "1")
	Long scenarioId,

	@Schema(description = "시나리오 이름", example = "Cafe Order")
	String name,

	@Schema(description = "레벨", example = "BEGINNER")
	String level
) {
	public static ScenarioSummaryResponse from(ScenarioSummaryInfo info) {
		return new ScenarioSummaryResponse(
			info.scenarioId(),
			info.name(),
			info.level()
		);
	}
}
