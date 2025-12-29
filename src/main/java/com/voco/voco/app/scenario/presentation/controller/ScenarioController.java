package com.voco.voco.app.scenario.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.scenario.application.usecase.CreateScenarioUseCase;
import com.voco.voco.app.scenario.presentation.controller.dto.in.CreateScenarioRequest;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Scenario", description = "시나리오 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

	private final CreateScenarioUseCase createScenarioUseCase;

	@Operation(summary = "시나리오 생성", description = "대화 시나리오를 생성합니다.")
	@PostMapping
	public ApiResponse<Long> createScenario(@Valid @RequestBody CreateScenarioRequest request) {
		Long scenarioId = createScenarioUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(scenarioId);
	}
}
