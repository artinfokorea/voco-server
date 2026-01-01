package com.voco.voco.app.call.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.call.application.usecase.GetCallDetailUseCase;
import com.voco.voco.app.call.application.usecase.GetCallsUseCase;
import com.voco.voco.app.call.application.usecase.dto.out.CallDetailInfo;
import com.voco.voco.app.call.presentation.controller.dto.in.GetCallsRequest;
import com.voco.voco.app.call.presentation.controller.dto.out.CallDetailResponse;
import com.voco.voco.app.call.presentation.controller.dto.out.CallHistoryResponse;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;
import com.voco.voco.common.dto.response.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Call", description = "통화 내역 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
public class CallController {

	private final GetCallsUseCase getCallsUseCase;
	private final GetCallDetailUseCase getCallDetailUseCase;

	@Operation(summary = "통화 내역 목록 조회", description = "회원의 통화 내역을 페이징하여 조회합니다.")
	@GetMapping
	public ApiResponse<PageResponse<CallHistoryResponse>> getCalls(
		@MemberId Long memberId,
		@ModelAttribute GetCallsRequest request
	) {
		return ApiResponse.success(
			PageResponse.from(
				getCallsUseCase.execute(memberId, request.getPageIndex(), request.size())
					.map(CallHistoryResponse::from)
			)
		);
	}

	@Operation(summary = "통화 상세 조회", description = "통화 상세 정보를 조회합니다.")
	@GetMapping("/{callId}")
	public ApiResponse<CallDetailResponse> getCallDetail(
		@MemberId Long memberId,
		@PathVariable Long callId
	) {
		CallDetailInfo result = getCallDetailUseCase.execute(callId, memberId);
		return ApiResponse.success(CallDetailResponse.from(result));
	}
}
