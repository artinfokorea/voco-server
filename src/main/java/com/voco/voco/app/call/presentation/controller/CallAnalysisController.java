package com.voco.voco.app.call.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.call.application.usecase.GetCallAnalysisUseCase;
import com.voco.voco.app.call.presentation.controller.dto.out.CallAnalysisResponse;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Call Analysis", description = "통화 분석 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/call-analyses")
@RequiredArgsConstructor
public class CallAnalysisController {

	private final GetCallAnalysisUseCase getCallAnalysisUseCase;

	@Operation(summary = "분석 결과 단건 조회", description = "통화 분석 결과를 조회합니다.")
	@GetMapping("/{id}")
	public ApiResponse<CallAnalysisResponse> getCallAnalysis(
		@MemberId Long memberId,
		@Parameter(description = "분석 결과 ID", example = "1")
		@PathVariable Long id
	) {
		CallAnalysisResponse response = CallAnalysisResponse.from(getCallAnalysisUseCase.execute(id, memberId));
		return ApiResponse.success(response);
	}
}
