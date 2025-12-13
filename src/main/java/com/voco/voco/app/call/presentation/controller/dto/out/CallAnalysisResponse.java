package com.voco.voco.app.call.presentation.controller.dto.out;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 분석 결과 응답")
public record CallAnalysisResponse(
	@Schema(description = "분석 결과 ID", example = "1")
	Long id,

	@Schema(description = "분석 내용 (JSON 형식)", example = "{\"pronunciation\": 85, \"grammar\": 90}")
	String content,

	@Schema(description = "총점", example = "85")
	Integer score,

	@Schema(description = "대화 분석 요약", example = "전반적으로 좋은 발음과 문법을 사용했습니다.")
	String summary
) {
	public static CallAnalysisResponse from(CallAnalysisInfo info) {
		return new CallAnalysisResponse(
			info.id(),
			info.content(),
			info.score(),
			info.summary()
		);
	}
}
