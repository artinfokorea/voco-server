package com.voco.voco.app.scenario.presentation.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.scenario.application.usecase.CreateScenarioUseCase;
import com.voco.voco.app.scenario.application.usecase.DeleteScenarioUseCase;
import com.voco.voco.app.scenario.application.usecase.GetScenarioDetailUseCase;
import com.voco.voco.app.scenario.application.usecase.GetScenariosUseCase;
import com.voco.voco.app.scenario.application.usecase.UpdateScenarioUseCase;
import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioDetailInfo;
import com.voco.voco.app.scenario.presentation.controller.dto.in.CreateScenarioRequest;
import com.voco.voco.app.scenario.presentation.controller.dto.in.GetScenariosRequest;
import com.voco.voco.app.scenario.presentation.controller.dto.in.UpdateScenarioRequest;
import com.voco.voco.app.scenario.presentation.controller.dto.out.ScenarioDetailResponse;
import com.voco.voco.app.scenario.presentation.controller.dto.out.ScenarioSummaryResponse;
import com.voco.voco.common.annotation.AdminId;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.common.dto.response.PageResponse;

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
	private final GetScenariosUseCase getScenariosUseCase;
	private final GetScenarioDetailUseCase getScenarioDetailUseCase;
	private final UpdateScenarioUseCase updateScenarioUseCase;
	private final DeleteScenarioUseCase deleteScenarioUseCase;

	@Operation(summary = "시나리오 목록 조회", description = "시나리오 목록을 페이징하여 조회합니다. 레벨로 필터링할 수 있습니다.")
	@GetMapping
	public ApiResponse<PageResponse<ScenarioSummaryResponse>> getScenarios(
		@MemberId Long memberId,
		@ModelAttribute GetScenariosRequest request
	) {
		return ApiResponse.success(
			PageResponse.from(
				getScenariosUseCase.execute(request.level(), request.getPageIndex(), request.size())
					.map(ScenarioSummaryResponse::from)
			)
		);
	}

	@Operation(summary = "시나리오 상세 조회", description = "시나리오 상세 정보를 조회합니다.")
	@GetMapping("/{scenarioId}")
	public ApiResponse<ScenarioDetailResponse> getScenarioDetail(
		@MemberId Long memberId,
		@PathVariable Long scenarioId
	) {
		ScenarioDetailInfo result = getScenarioDetailUseCase.execute(scenarioId);
		return ApiResponse.success(ScenarioDetailResponse.from(result));
	}

	@Operation(summary = "시나리오 생성", description = "대화 시나리오를 생성합니다. (관리자 전용)")
	@PostMapping
	public ApiResponse<Long> createScenario(
		@AdminId Long adminId,
		@Valid @RequestBody CreateScenarioRequest request
	) {
		Long scenarioId = createScenarioUseCase.execute(request.toUseCaseDto());
		return ApiResponse.success(scenarioId);
	}

	@Operation(summary = "시나리오 수정", description = "대화 시나리오를 수정합니다. (관리자 전용)")
	@PutMapping("/{scenarioId}")
	public ApiResponse<Void> updateScenario(
		@AdminId Long adminId,
		@PathVariable Long scenarioId,
		@Valid @RequestBody UpdateScenarioRequest request
	) {
		updateScenarioUseCase.execute(request.toUseCaseDto(scenarioId));
		return ApiResponse.success(null);
	}

	@Operation(summary = "시나리오 삭제", description = "대화 시나리오를 삭제합니다. (관리자 전용)")
	@DeleteMapping("/{scenarioId}")
	public ApiResponse<Void> deleteScenario(
		@AdminId Long adminId,
		@PathVariable Long scenarioId
	) {
		deleteScenarioUseCase.execute(scenarioId);
		return ApiResponse.success(null);
	}
}
