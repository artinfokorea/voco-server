package com.voco.voco.app.call.application.usecase.dto.out;

import java.util.List;

import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;

public record CallAnalysisInfo(
	Long id,
	CallAnalysisGrade grade,
	Integer overallScore,
	Integer taskCompletionScore,
	String taskCompletionSummary,
	Integer languageAccuracyScore,
	String languageAccuracySummary,
	List<ConversationInfo> conversation,
	FeedbackInfo feedback
) {
	public record ConversationInfo(
		String role,
		String content,
		ConversationErrorInfo error
	) {
	}

	public record ConversationErrorInfo(
		String errorType,
		String errorSubtype,
		String errorSegment,
		String correction,
		String explanation
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
		List<ConversationInfo> conversationInfos = null;
		if (entity.getConversation() != null) {
			conversationInfos = entity.getConversation().stream()
				.map(c -> new ConversationInfo(
					c.role(),
					c.content(),
					c.error() != null
						? new ConversationErrorInfo(
							c.error().errorType(),
							c.error().errorSubtype(),
							c.error().errorSegment(),
							c.error().correction(),
							c.error().explanation()
						)
						: null
				))
				.toList();
		}

		FeedbackInfo feedbackInfo = null;
		if (entity.getFeedback() != null) {
			feedbackInfo = new FeedbackInfo(
				entity.getFeedback().getStrengths(),
				entity.getFeedback().getImprovements(),
				entity.getFeedback().getFocusAreas(),
				entity.getFeedback().getTips()
			);
		}

		return new CallAnalysisInfo(
			entity.getId(),
			entity.getGrade(),
			entity.getOverallScore(),
			entity.getTaskCompletionScore(),
			entity.getTaskCompletionSummary(),
			entity.getLanguageAccuracyScore(),
			entity.getLanguageAccuracySummary(),
			conversationInfos,
			feedbackInfo
		);
	}
}
