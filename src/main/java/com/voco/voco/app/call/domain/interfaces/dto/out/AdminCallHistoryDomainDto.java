package com.voco.voco.app.call.domain.interfaces.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.scenario.domain.model.Level;

public record AdminCallHistoryDomainDto(
	Long callId,
	String scenarioName,
	String memberName,
	Long memberId,
	Level scenarioLevel,
	LocalDateTime analysisCreatedAt,
	CallAnalysisEntity analysis
) {
}
