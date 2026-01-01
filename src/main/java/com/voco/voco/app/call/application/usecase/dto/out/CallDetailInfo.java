package com.voco.voco.app.call.application.usecase.dto.out;

import java.time.LocalDateTime;
import java.util.List;

import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;
import com.voco.voco.app.call.domain.interfaces.dto.out.CallDetailDomainDto;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.scenario.domain.model.Level;

public record CallDetailInfo(
	LocalDateTime createdAt,
	String scenarioName,
	Level scenarioLevel,
	CallAnalysisGrade grade,
	Integer overallScore,
	List<ConversationEntryInfo> conversation,
	Integer taskCompletionScore,
	String taskCompletionSummary,
	Integer languageAccuracyScore,
	String languageAccuracySummary,
	FeedbackInfo feedback
) {
	public static CallDetailInfo from(CallDetailDomainDto dto) {
		CallAnalysisEntity analysis = dto.analysis();

		List<ConversationEntryInfo> conversationInfo = null;
		FeedbackInfo feedbackInfo = null;
		CallAnalysisGrade grade = null;
		Integer overallScore = null;
		Integer taskCompletionScore = null;
		String taskCompletionSummary = null;
		Integer languageAccuracyScore = null;
		String languageAccuracySummary = null;

		if (analysis != null) {
			grade = analysis.getGrade();
			overallScore = analysis.getOverallScore();
			taskCompletionScore = analysis.getTaskCompletionScore();
			taskCompletionSummary = analysis.getTaskCompletionSummary();
			languageAccuracyScore = analysis.getLanguageAccuracyScore();
			languageAccuracySummary = analysis.getLanguageAccuracySummary();

			if (analysis.getConversation() != null) {
				conversationInfo = analysis.getConversation().stream()
					.map(ConversationEntryInfo::from)
					.toList();
			}

			if (analysis.getFeedback() != null) {
				feedbackInfo = FeedbackInfo.from(analysis.getFeedback());
			}
		}

		return new CallDetailInfo(
			dto.createdAt(),
			dto.scenarioName(),
			dto.scenarioLevel(),
			grade,
			overallScore,
			conversationInfo,
			taskCompletionScore,
			taskCompletionSummary,
			languageAccuracyScore,
			languageAccuracySummary,
			feedbackInfo
		);
	}

	public record ConversationEntryInfo(
		String role,
		String content,
		ConversationErrorInfo error
	) {
		public static ConversationEntryInfo from(CallAnalysisEntity.ConversationEntry entry) {
			ConversationErrorInfo errorInfo = null;
			if (entry.error() != null) {
				errorInfo = ConversationErrorInfo.from(entry.error());
			}
			return new ConversationEntryInfo(entry.role(), entry.content(), errorInfo);
		}
	}

	public record ConversationErrorInfo(
		String errorType,
		String errorSubtype,
		String errorSegment,
		String correction,
		String explanation
	) {
		public static ConversationErrorInfo from(CallAnalysisEntity.ConversationError error) {
			return new ConversationErrorInfo(
				error.errorType(),
				error.errorSubtype(),
				error.errorSegment(),
				error.correction(),
				error.explanation()
			);
		}
	}

	public record FeedbackInfo(
		List<String> strengths,
		List<String> improvements,
		List<String> focusAreas,
		List<String> tips
	) {
		public static FeedbackInfo from(com.voco.voco.app.call.domain.model.FeedbackEmbeddable feedback) {
			return new FeedbackInfo(
				feedback.getStrengths(),
				feedback.getImprovements(),
				feedback.getFocusAreas(),
				feedback.getTips()
			);
		}
	}
}
