package com.voco.voco.app.call.domain.interfaces.dto;

import java.time.LocalDateTime;

import com.voco.voco.app.scenario.domain.model.Category;
import com.voco.voco.app.scenario.domain.model.Level;

public record CallWithScenarioDto(
	Long callId,
	Long scenarioId,
	String scenarioTitle,
	String scenarioDescription,
	Level scenarioLevel,
	Category scenarioCategory,
	Long analysisId,
	LocalDateTime createdAt
) {
}
