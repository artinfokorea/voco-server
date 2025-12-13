package com.voco.voco.app.call.application.usecase.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.domain.interfaces.dto.CallWithScenarioDto;
import com.voco.voco.app.scenario.domain.model.Category;
import com.voco.voco.app.scenario.domain.model.Level;

public record CallInfo(
	Long callId,
	Long scenarioId,
	String scenarioTitle,
	String scenarioDescription,
	Level scenarioLevel,
	Category scenarioCategory,
	Long analysisId,
	LocalDateTime createdAt
) {
	public static CallInfo from(CallWithScenarioDto dto) {
		return new CallInfo(
			dto.callId(),
			dto.scenarioId(),
			dto.scenarioTitle(),
			dto.scenarioDescription(),
			dto.scenarioLevel(),
			dto.scenarioCategory(),
			dto.analysisId(),
			dto.createdAt()
		);
	}
}
