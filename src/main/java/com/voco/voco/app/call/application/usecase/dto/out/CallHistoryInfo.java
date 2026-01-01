package com.voco.voco.app.call.application.usecase.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;
import com.voco.voco.app.call.domain.interfaces.dto.out.CallHistoryDomainDto;

public record CallHistoryInfo(
	Long callId,
	LocalDateTime createdAt,
	String scenarioName,
	CallAnalysisGrade grade
) {
	public static CallHistoryInfo from(CallHistoryDomainDto dto) {
		return new CallHistoryInfo(
			dto.callId(),
			dto.createdAt(),
			dto.scenarioName(),
			dto.grade()
		);
	}
}
