package com.voco.voco.app.call.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.call.application.usecase.GetAdminCallAnalysisRawUseCase;
import com.voco.voco.app.call.application.usecase.GetAdminCallsUseCase;
import com.voco.voco.app.call.presentation.controller.dto.in.GetCallsRequest;
import com.voco.voco.app.call.presentation.controller.dto.out.AdminCallHistoryResponse;
import com.voco.voco.app.call.presentation.controller.dto.out.CallAnalysisRawResponse;
import com.voco.voco.common.annotation.AdminId;
import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.common.dto.response.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin Call", description = "관리자 통화 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin/calls")
@RequiredArgsConstructor
public class AdminCallController {

	private final GetAdminCallsUseCase getAdminCallsUseCase;
	private final GetAdminCallAnalysisRawUseCase getAdminCallAnalysisRawUseCase;

	@Operation(summary = "통화 내역 목록 조회 (관리자)", description = "모든 회원의 통화 내역을 페이징하여 조회합니다.")
	@GetMapping
	public ApiResponse<PageResponse<AdminCallHistoryResponse>> getAdminCalls(
		@AdminId Long adminId,
		@ModelAttribute GetCallsRequest request
	) {
		return ApiResponse.success(
			PageResponse.from(
				getAdminCallsUseCase.execute(request.getPageIndex(), request.size())
					.map(AdminCallHistoryResponse::from)
			)
		);
	}

	@Operation(summary = "통화 분석 Raw 데이터 조회 (관리자)", description = "통화 분석 Raw 데이터를 조회합니다.")
	@GetMapping("/{callId}/analysis-raw")
	public ApiResponse<CallAnalysisRawResponse> getAdminCallAnalysisRaw(
		@AdminId Long adminId,
		@PathVariable Long callId
	) {
		return ApiResponse.success(
			CallAnalysisRawResponse.from(getAdminCallAnalysisRawUseCase.execute(callId))
		);
	}
}
