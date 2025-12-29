package com.voco.voco.app.call.presentation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voco.voco.app.call.application.usecase.CreateLiveKitTokenUseCase;
import com.voco.voco.app.call.application.usecase.dto.out.LiveKitTokenInfo;
import com.voco.voco.app.call.presentation.controller.dto.in.CreateLiveKitTokenRequest;
import com.voco.voco.app.call.presentation.controller.dto.out.LiveKitTokenResponse;
import com.voco.voco.common.annotation.MemberId;
import com.voco.voco.common.dto.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "LiveKit", description = "LiveKit 토큰 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/livekit")
@RequiredArgsConstructor
public class LiveKitController {

	private final CreateLiveKitTokenUseCase createLiveKitTokenUseCase;

	@Operation(summary = "토큰 발급", description = "LiveKit 접속을 위한 토큰을 발급합니다. 시나리오 정보가 메타데이터로 포함됩니다.")
	@PostMapping("/token")
	public ApiResponse<LiveKitTokenResponse> createToken(
		@MemberId Long memberId,
		@Valid @RequestBody CreateLiveKitTokenRequest request
	) {
		LiveKitTokenInfo tokenInfo = createLiveKitTokenUseCase.execute(memberId, request.scenarioId());
		return ApiResponse.success(LiveKitTokenResponse.from(tokenInfo));
	}
}
