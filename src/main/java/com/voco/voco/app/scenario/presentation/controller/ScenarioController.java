package com.voco.voco.app.scenario.presentation.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.scenario.application.usecase.GetScenariosUseCase;
import com.voco.voco.app.scenario.presentation.controller.dto.in.GetScenariosRequest;
import com.voco.voco.app.scenario.presentation.controller.dto.out.ScenarioResponse;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Scenario", description = "시나리오 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scenarios")
public class ScenarioController {

	private final GetScenariosUseCase getScenariosUseCase;

	@Operation(summary = "시나리오 목록 조회", description = "시나리오 목록을 조회합니다. 레벨로 필터링할 수 있습니다.")
	@GetMapping
	public ApiResponse<List<ScenarioResponse>> getScenarios(
		@ParameterObject GetScenariosRequest request
	) {
		List<ScenarioResponse> responses = getScenariosUseCase.execute(request.level())
			.stream()
			.map(ScenarioResponse::from)
			.toList();
		return ApiResponse.success(responses);
	}
}
