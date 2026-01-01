package com.voco.voco.app.call.presentation.controller.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.application.usecase.dto.out.AdminCallHistoryInfo;
import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 통화 내역 응답")
public record AdminCallHistoryResponse(
	@Schema(description = "통화 ID")
	Long callId,

	@Schema(description = "시나리오 제목")
	String scenarioName,

	@Schema(description = "사용자 이름")
	String memberName,

	@Schema(description = "사용자 ID")
	Long memberId,

	@Schema(description = "시나리오 레벨")
	Level scenarioLevel,

	@Schema(description = "분석 생성일")
	LocalDateTime analysisCreatedAt,

	@Schema(description = "대화 시간 (초)")
	Integer conversationDuration,

	@Schema(description = "대화 턴수")
	Integer conversationTurnCount
) {
	public static AdminCallHistoryResponse from(AdminCallHistoryInfo info) {
		return new AdminCallHistoryResponse(
			info.callId(),
			info.scenarioName(),
			info.memberName(),
			info.memberId(),
			info.scenarioLevel(),
			info.analysisCreatedAt(),
			info.conversationDuration(),
			info.conversationTurnCount()
		);
	}
}
