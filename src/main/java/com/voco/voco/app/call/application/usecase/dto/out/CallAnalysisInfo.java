package com.voco.voco.app.call.application.usecase.dto.out;

import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.domain.model.CallAnalysisEntity;

public record CallAnalysisInfo(
	Long id,
	Map<String, Object> taskCompletion,
	Integer totalUserUtterances,
	Integer correctUtterances,
	List<Map<String, Object>> utteranceAnalyses,
	List<Map<String, Object>> errors,
	Map<String, Object> errorSummary,
	ScoringInfo scoring,
	FeedbackInfo feedback,
	String briefDescription
) {
	public record ScoringInfo(
		Integer baseScore,
		Integer totalDeduction,
		Integer criticalDeduction,
		Integer majorDeduction,
		Integer minorDeduction,
		Integer levelAdjustment,
		Integer finalScore,
		String rating
	) {
	}

	public record FeedbackInfo(
		List<String> strengths,
		List<String> improvements,
		List<String> focusAreas,
		List<String> tips
	) {
	}

	public static CallAnalysisInfo from(CallAnalysisEntity entity) {
		return new CallAnalysisInfo(
			entity.getId(),
			entity.getTaskCompletion(),
			entity.getTotalUserUtterances(),
			entity.getCorrectUtterances(),
			entity.getUtteranceAnalyses(),
			entity.getErrors(),
			entity.getErrorSummary(),
			entity.getScoring() != null
				? new ScoringInfo(
					entity.getScoring().getBaseScore(),
					entity.getScoring().getTotalDeduction(),
					entity.getScoring().getCriticalDeduction(),
					entity.getScoring().getMajorDeduction(),
					entity.getScoring().getMinorDeduction(),
					entity.getScoring().getLevelAdjustment(),
					entity.getScoring().getFinalScore(),
					entity.getScoring().getRating()
				)
				: null,
			entity.getFeedback() != null
				? new FeedbackInfo(
					entity.getFeedback().getStrengths(),
					entity.getFeedback().getImprovements(),
					entity.getFeedback().getFocusAreas(),
					entity.getFeedback().getTips()
				)
				: null,
			entity.getBriefDescription()
		);
	}
}
