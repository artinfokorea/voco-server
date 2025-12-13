package com.voco.voco.app.call.application.usecase.dto.out;

import com.voco.voco.app.call.domain.model.CallAnalysisEntity;

public record CallAnalysisInfo(
	Long id,
	String content,
	Integer score,
	String summary
) {
	public static CallAnalysisInfo from(CallAnalysisEntity entity) {
		return new CallAnalysisInfo(
			entity.getId(),
			entity.getContent(),
			entity.getScore(),
			entity.getSummary()
		);
	}
}
