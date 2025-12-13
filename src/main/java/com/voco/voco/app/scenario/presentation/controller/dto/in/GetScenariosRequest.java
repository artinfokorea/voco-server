package com.voco.voco.app.scenario.presentation.controller.dto.in;

import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.Parameter;

public record GetScenariosRequest(
	@Parameter(description = "난이도 필터 (BEGINNER, INTERMEDIATE, ADVANCED)")
	Level level
) {
}
