package com.voco.voco.app.call.presentation.controller.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 분석 결과 생성 응답")
public record CreateCallAnalysisResponse(
	@Schema(description = "분석 결과 ID")
	Long analysisId
) {
}
