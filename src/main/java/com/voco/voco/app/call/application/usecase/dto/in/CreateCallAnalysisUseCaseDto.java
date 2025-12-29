package com.voco.voco.app.call.application.usecase.dto.in;

import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.call.domain.model.FeedbackEmbeddable;
import com.voco.voco.app.call.domain.model.ScoringEmbeddable;

public record CreateCallAnalysisUseCaseDto(
	Map<String, Object> taskCompletion,
	Integer totalUserUtterances,
	Integer correctUtterances,
	List<Map<String, Object>> utteranceAnalyses,
	List<Map<String, Object>> errors,
	Map<String, Object> errorSummary,
	ScoringDto scoring,
	FeedbackDto feedback,
	String briefDescription
) {
	public record ScoringDto(
		Integer baseScore,
		Integer totalDeduction,
		Integer criticalDeduction,
		Integer majorDeduction,
		Integer minorDeduction,
		Integer levelAdjustment,
		Integer finalScore,
		String rating
	) {
		public ScoringEmbeddable toEmbeddable() {
			return ScoringEmbeddable.create(
				baseScore,
				totalDeduction,
				criticalDeduction,
				majorDeduction,
				minorDeduction,
				levelAdjustment,
				finalScore,
				rating
			);
		}
	}

	public record FeedbackDto(
		List<String> strengths,
		List<String> improvements,
		List<String> focusAreas,
		List<String> tips
	) {
		public FeedbackEmbeddable toEmbeddable() {
			return FeedbackEmbeddable.create(strengths, improvements, focusAreas, tips);
		}
	}

	public CallAnalysisEntity toEntity() {
		return CallAnalysisEntity.create(
			taskCompletion,
			totalUserUtterances,
			correctUtterances,
			utteranceAnalyses,
			errors,
			errorSummary,
			scoring.toEmbeddable(),
			feedback.toEmbeddable(),
			briefDescription
		);
	}
}
