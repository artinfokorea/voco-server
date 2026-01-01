package com.voco.voco.app.call.application.usecase.dto.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.domain.model.CallAnalysisEntity;
import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;
import com.voco.voco.app.call.domain.model.FeedbackEmbeddable;

public record CreateCallAnalysisUseCaseDto(
	List<ConversationDto> conversation,
	TaskCompletionDto taskCompletion,
	LanguageAccuracyDto languageAccuracy
) {
	public record ConversationDto(
		String role,
		String content
	) {
	}

	public record TaskCompletionDto(
		TaskSummaryDto summary,
		Object slotAnalysis,
		Object stateAnalysis,
		Object completionAnalysis,
		Object taskSuccess
	) {
		public Map<String, Object> toRawMap() {
			Map<String, Object> map = new HashMap<>();
			if (summary != null) {
				map.put("summary", summary.toRawMap());
			}
			if (slotAnalysis != null) {
				map.put("slot_analysis", slotAnalysis);
			}
			if (stateAnalysis != null) {
				map.put("state_analysis", stateAnalysis);
			}
			if (completionAnalysis != null) {
				map.put("completion_analysis", completionAnalysis);
			}
			if (taskSuccess != null) {
				map.put("task_success", taskSuccess);
			}
			return map;
		}
	}

	public record TaskSummaryDto(
		Integer overallCompletionScore,
		String status,
		String briefDescription
	) {
		public Map<String, Object> toRawMap() {
			Map<String, Object> map = new HashMap<>();
			if (overallCompletionScore != null) {
				map.put("overall_completion_score", overallCompletionScore);
			}
			if (status != null) {
				map.put("status", status);
			}
			if (briefDescription != null) {
				map.put("brief_description", briefDescription);
			}
			return map;
		}
	}

	public record LanguageAccuracyDto(
		ScoringDto scoring,
		FeedbackDto feedback,
		String briefDescription,
		List<ErrorDto> errors,
		Integer totalUserUtterances,
		Integer correctUtterances,
		Object utteranceAnalyses,
		Object errorSummary
	) {
		public Map<String, Object> toRawMap() {
			Map<String, Object> map = new HashMap<>();
			if (scoring != null) {
				map.put("scoring", scoring.toRawMap());
			}
			if (feedback != null) {
				map.put("feedback", feedback.toRawMap());
			}
			if (briefDescription != null) {
				map.put("brief_description", briefDescription);
			}
			if (errors != null) {
				map.put("errors", errors.stream().map(ErrorDto::toRawMap).toList());
			}
			if (totalUserUtterances != null) {
				map.put("total_user_utterances", totalUserUtterances);
			}
			if (correctUtterances != null) {
				map.put("correct_utterances", correctUtterances);
			}
			if (utteranceAnalyses != null) {
				map.put("utterance_analyses", utteranceAnalyses);
			}
			if (errorSummary != null) {
				map.put("error_summary", errorSummary);
			}
			return map;
		}
	}

	public record ScoringDto(
		Integer finalScore,
		String rating,
		Integer baseScore,
		Integer totalDeduction,
		Integer levelAdjustment
	) {
		public Map<String, Object> toRawMap() {
			Map<String, Object> map = new HashMap<>();
			if (finalScore != null) {
				map.put("final_score", finalScore);
			}
			if (rating != null) {
				map.put("rating", rating);
			}
			if (baseScore != null) {
				map.put("base_score", baseScore);
			}
			if (totalDeduction != null) {
				map.put("total_deduction", totalDeduction);
			}
			if (levelAdjustment != null) {
				map.put("level_adjustment", levelAdjustment);
			}
			return map;
		}
	}

	public record FeedbackDto(
		List<String> strengths,
		List<String> improvements,
		List<String> focusAreas,
		List<String> tips
	) {
		public Map<String, Object> toRawMap() {
			Map<String, Object> map = new HashMap<>();
			if (strengths != null) {
				map.put("strengths", strengths);
			}
			if (improvements != null) {
				map.put("improvements", improvements);
			}
			if (focusAreas != null) {
				map.put("focus_areas", focusAreas);
			}
			if (tips != null) {
				map.put("tips", tips);
			}
			return map;
		}
	}

	public record ErrorDto(
		Integer turn,
		String errorType,
		String errorSubtype,
		String errorSegment,
		String correction,
		String explanation,
		String severity,
		String originalText
	) {
		public Map<String, Object> toRawMap() {
			Map<String, Object> map = new HashMap<>();
			if (turn != null) {
				map.put("turn", turn);
			}
			if (errorType != null) {
				map.put("error_type", errorType);
			}
			if (errorSubtype != null) {
				map.put("error_subtype", errorSubtype);
			}
			if (errorSegment != null) {
				map.put("error_segment", errorSegment);
			}
			if (correction != null) {
				map.put("correction", correction);
			}
			if (explanation != null) {
				map.put("explanation", explanation);
			}
			if (severity != null) {
				map.put("severity", severity);
			}
			if (originalText != null) {
				map.put("original_text", originalText);
			}
			return map;
		}
	}

	public CallAnalysisEntity toAnalysisEntity() {
		List<CallAnalysisEntity.ConversationEntry> conversationEntries = buildConversationEntries();

		Integer taskCompletionScore = taskCompletion != null && taskCompletion.summary() != null
			? taskCompletion.summary().overallCompletionScore() : null;
		String taskCompletionSummary = taskCompletion != null && taskCompletion.summary() != null
			? taskCompletion.summary().briefDescription() : null;
		Integer languageAccuracyScore = languageAccuracy != null && languageAccuracy.scoring() != null
			? languageAccuracy.scoring().finalScore() : null;
		String languageAccuracySummary = languageAccuracy != null
			? languageAccuracy.briefDescription() : null;
		FeedbackEmbeddable feedback = extractFeedback();

		return CallAnalysisEntity.create(
			conversationEntries,
			taskCompletionScore,
			taskCompletionSummary,
			languageAccuracyScore,
			languageAccuracySummary,
			feedback
		);
	}

	public CallAnalysisRawEntity toRawEntity(Long analysisId) {
		List<CallAnalysisRawEntity.ConversationRaw> conversationRaw = conversation.stream()
			.map(c -> new CallAnalysisRawEntity.ConversationRaw(c.role(), c.content()))
			.toList();

		return CallAnalysisRawEntity.create(
			analysisId,
			conversationRaw,
			taskCompletion != null ? taskCompletion.toRawMap() : null,
			languageAccuracy != null ? languageAccuracy.toRawMap() : null
		);
	}

	private FeedbackEmbeddable extractFeedback() {
		if (languageAccuracy == null || languageAccuracy.feedback() == null) {
			return null;
		}
		FeedbackDto feedback = languageAccuracy.feedback();
		return FeedbackEmbeddable.create(
			feedback.strengths(),
			feedback.improvements(),
			feedback.focusAreas(),
			feedback.tips()
		);
	}

	private List<CallAnalysisEntity.ConversationEntry> buildConversationEntries() {
		Map<Integer, ErrorDto> errorsByTurn = new HashMap<>();

		if (languageAccuracy != null && languageAccuracy.errors() != null) {
			for (ErrorDto error : languageAccuracy.errors()) {
				if (error.turn() != null) {
					errorsByTurn.put(error.turn(), error);
				}
			}
		}

		List<CallAnalysisEntity.ConversationEntry> entries = new ArrayList<>();
		for (int i = 0; i < conversation.size(); i++) {
			ConversationDto conv = conversation.get(i);
			int turn = i + 1;

			CallAnalysisEntity.ConversationError error = null;
			if (errorsByTurn.containsKey(turn)) {
				ErrorDto errorDto = errorsByTurn.get(turn);
				error = new CallAnalysisEntity.ConversationError(
					errorDto.errorType(),
					errorDto.errorSubtype(),
					errorDto.errorSegment(),
					errorDto.correction(),
					errorDto.explanation()
				);
			}

			entries.add(new CallAnalysisEntity.ConversationEntry(conv.role(), conv.content(), error));
		}

		return entries;
	}
}
