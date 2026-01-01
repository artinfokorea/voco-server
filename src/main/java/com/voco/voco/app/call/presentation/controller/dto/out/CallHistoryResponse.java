package com.voco.voco.app.call.presentation.controller.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.application.usecase.dto.out.CallHistoryInfo;
import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 내역 응답")
public record CallHistoryResponse(
	@Schema(description = "통화 ID")
	Long callId,

	@Schema(description = "통화 생성 일시")
	LocalDateTime createdAt,

	@Schema(description = "시나리오 제목")
	String scenarioName,

	@Schema(description = "분석 등급 (분석이 없으면 null)")
	CallAnalysisGrade grade
) {
	public static CallHistoryResponse from(CallHistoryInfo info) {
		return new CallHistoryResponse(
			info.callId(),
			info.createdAt(),
			info.scenarioName(),
			info.grade()
		);
	}
}
