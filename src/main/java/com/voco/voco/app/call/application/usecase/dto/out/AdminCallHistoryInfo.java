package com.voco.voco.app.call.application.usecase.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.domain.interfaces.dto.out.AdminCallHistoryDomainDto;
import com.voco.voco.app.scenario.domain.model.Level;

public record AdminCallHistoryInfo(
	Long callId,
	String scenarioName,
	String memberName,
	Long memberId,
	Level scenarioLevel,
	LocalDateTime analysisCreatedAt,
	Integer conversationDuration,
	Integer conversationTurnCount
) {
	public static AdminCallHistoryInfo from(AdminCallHistoryDomainDto dto) {
		Integer turnCount = null;
		if (dto.analysis() != null && dto.analysis().getConversation() != null) {
			turnCount = dto.analysis().getConversation().size();
		}

		return new AdminCallHistoryInfo(
			dto.callId(),
			dto.scenarioName(),
			dto.memberName(),
			dto.memberId(),
			dto.scenarioLevel(),
			dto.analysisCreatedAt(),
			null,
			turnCount
		);
	}
}
