package com.voco.voco.app.call.presentation.controller.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.application.usecase.dto.out.CallInfo;
import com.voco.voco.app.scenario.domain.model.Category;
import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 내역 응답")
public record CallResponse(
	@Schema(description = "통화 ID", example = "1")
	Long callId,

	@Schema(description = "시나리오 ID", example = "1")
	Long scenarioId,

	@Schema(description = "시나리오 제목", example = "카페에서 음료 주문하기")
	String scenarioTitle,

	@Schema(description = "시나리오 설명", example = "카페에서 원하는 음료를 영어로 주문하는 연습입니다.")
	String scenarioDescription,

	@Schema(description = "시나리오 난이도", example = "BEGINNER")
	Level scenarioLevel,

	@Schema(description = "시나리오 카테고리", example = "DAILY")
	Category scenarioCategory,

	@Schema(description = "분석 결과 ID (분석 완료 시 존재)", example = "1")
	Long analysisId,

	@Schema(description = "통화 생성 일시", example = "2025-12-14T00:40:00")
	LocalDateTime createdAt
) {
	public static CallResponse from(CallInfo info) {
		return new CallResponse(
			info.callId(),
			info.scenarioId(),
			info.scenarioTitle(),
			info.scenarioDescription(),
			info.scenarioLevel(),
			info.scenarioCategory(),
			info.analysisId(),
			info.createdAt()
		);
	}
}
