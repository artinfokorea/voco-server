package com.voco.voco.app.call.presentation.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.call.application.usecase.CreateCallAnalysisUseCase;
import com.voco.voco.app.call.presentation.controller.dto.in.CreateCallAnalysisRequest;
import com.voco.voco.app.call.presentation.controller.dto.out.CreateCallAnalysisResponse;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Call Analysis", description = "통화 분석 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
public class CallAnalysisController {

	private final CreateCallAnalysisUseCase createCallAnalysisUseCase;

	@Operation(summary = "통화 분석 결과 저장", description = "통화 분석 결과를 저장합니다.")
	@PostMapping("/{callId}/analyses")
	public ApiResponse<CreateCallAnalysisResponse> createCallAnalysis(
		@PathVariable Long callId,
		@Valid @RequestBody CreateCallAnalysisRequest request
	) {
		Long analysisId = createCallAnalysisUseCase.execute(callId, request.toUseCaseDto());
		return ApiResponse.success(new CreateCallAnalysisResponse(analysisId));
	}
}
