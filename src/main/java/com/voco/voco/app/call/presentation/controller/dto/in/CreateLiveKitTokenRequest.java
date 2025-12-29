package com.voco.voco.app.call.presentation.controller.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "LiveKit 토큰 생성 요청")
public record CreateLiveKitTokenRequest(
	@Schema(description = "시나리오 ID", example = "1")
	@NotNull(message = "시나리오 ID는 필수입니다.")
	Long scenarioId
) {
}
