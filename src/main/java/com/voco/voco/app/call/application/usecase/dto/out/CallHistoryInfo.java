package com.voco.voco.app.call.application.usecase.dto.out;

import java.time.LocalDateTime;

import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;

public record CallHistoryInfo(
	Long callId,
	LocalDateTime createdAt,
	String scenarioName,
	CallAnalysisGrade grade
) {
}
