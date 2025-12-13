package com.voco.voco.app.call.presentation.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.call.application.usecase.GetCallsUseCase;
import com.voco.voco.app.call.presentation.controller.dto.in.GetCallsRequest;
import com.voco.voco.app.call.presentation.controller.dto.out.CallResponse;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;

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

	@Operation(summary = "통화 내역 조회", description = "로그인한 사용자의 통화 내역을 페이징하여 조회합니다. 최신순으로 정렬됩니다.")
	@GetMapping
	public ApiResponse<Page<CallResponse>> getCalls(
		@MemberId Long memberId,
		@ParameterObject GetCallsRequest request
	) {
		Page<CallResponse> response = getCallsUseCase.execute(memberId, request.toPageable())
			.map(CallResponse::from);
		return ApiResponse.success(response);
	}
}
