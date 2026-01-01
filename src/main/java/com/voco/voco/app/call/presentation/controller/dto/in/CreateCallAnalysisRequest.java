package com.voco.voco.app.call.presentation.controller.dto.in;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.application.usecase.dto.in.CreateCallAnalysisUseCaseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "통화 분석 결과 생성 요청")
public record CreateCallAnalysisRequest(
	@Schema(description = "대화 내역")
	@NotEmpty(message = "대화 내역은 필수입니다")
	@Valid
	List<ConversationRequest> conversation,

	@Schema(description = "과제 완료 분석")
	@NotNull(message = "과제 완료 분석은 필수입니다")
	@Valid
	TaskCompletionRequest taskCompletion,

	@Schema(description = "언어 정확도 분석")
	@NotNull(message = "언어 정확도 분석은 필수입니다")
	@Valid
	LanguageAccuracyRequest languageAccuracy
) {
	@Schema(description = "대화 항목")
	public record ConversationRequest(
		@Schema(description = "역할 (user/assistant)")
		@NotBlank(message = "역할은 필수입니다")
		String role,

		@Schema(description = "대화 내용")
		@NotBlank(message = "대화 내용은 필수입니다")
		String content
	) {
	}

	@Schema(description = "과제 완료 분석")
	public record TaskCompletionRequest(
		@Schema(description = "과제 완료 요약")
		@NotNull(message = "과제 완료 요약은 필수입니다")
		@Valid
		TaskSummaryRequest summary,

		@Schema(description = "슬롯 분석")
		Object slotAnalysis,

		@Schema(description = "상태 분석")
		Object stateAnalysis,

		@Schema(description = "완료 분석")
		Object completionAnalysis,

		@Schema(description = "과제 성공 여부")
		Object taskSuccess
	) {
		public Map<String, Object> toMap() {
			Map<String, Object> map = new HashMap<>();
			if (summary != null) {
				map.put("summary", summary.toMap());
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

	@Schema(description = "과제 완료 요약")
	public record TaskSummaryRequest(
		@Schema(description = "전체 완료 점수 (0-100)")
		@NotNull(message = "전체 완료 점수는 필수입니다")
		Integer overallCompletionScore,

		@Schema(description = "상태 (completed/incomplete)")
		String status,

		@Schema(description = "간략 설명")
		@NotBlank(message = "간략 설명은 필수입니다")
		String briefDescription
	) {
		public Map<String, Object> toMap() {
			Map<String, Object> map = new HashMap<>();
			map.put("overall_completion_score", overallCompletionScore);
			if (status != null) {
				map.put("status", status);
			}
			map.put("brief_description", briefDescription);
			return map;
		}
	}

	@Schema(description = "언어 정확도 분석")
	public record LanguageAccuracyRequest(
		@Schema(description = "점수")
		@NotNull(message = "점수 정보는 필수입니다")
		@Valid
		ScoringRequest scoring,

		@Schema(description = "피드백")
		@NotNull(message = "피드백은 필수입니다")
		@Valid
		FeedbackRequest feedback,

		@Schema(description = "간략 설명")
		@NotBlank(message = "간략 설명은 필수입니다")
		String briefDescription,

		@Schema(description = "오류 목록")
		List<@Valid ErrorRequest> errors,

		@Schema(description = "총 사용자 발화 수")
		Integer totalUserUtterances,

		@Schema(description = "정확한 발화 수")
		Integer correctUtterances,

		@Schema(description = "발화 분석")
		Object utteranceAnalyses,

		@Schema(description = "오류 요약")
		Object errorSummary
	) {
		public Map<String, Object> toMap() {
			Map<String, Object> map = new HashMap<>();
			if (scoring != null) {
				map.put("scoring", scoring.toMap());
			}
			if (feedback != null) {
				map.put("feedback", feedback.toMap());
			}
			if (briefDescription != null) {
				map.put("brief_description", briefDescription);
			}
			if (errors != null) {
				map.put("errors", errors.stream().map(ErrorRequest::toMap).toList());
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

	@Schema(description = "점수 정보")
	public record ScoringRequest(
		@Schema(description = "최종 점수 (0-100)")
		@NotNull(message = "최종 점수는 필수입니다")
		Integer finalScore,

		@Schema(description = "등급")
		String rating,

		@Schema(description = "기본 점수")
		Integer baseScore,

		@Schema(description = "총 감점")
		Integer totalDeduction,

		@Schema(description = "레벨 조정")
		Integer levelAdjustment
	) {
		public Map<String, Object> toMap() {
			Map<String, Object> map = new HashMap<>();
			map.put("final_score", finalScore);
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

	@Schema(description = "피드백 정보")
	public record FeedbackRequest(
		@Schema(description = "강점")
		@NotEmpty(message = "강점 목록은 필수입니다")
		List<String> strengths,

		@Schema(description = "개선점")
		@NotEmpty(message = "개선점 목록은 필수입니다")
		List<String> improvements,

		@Schema(description = "집중 영역")
		@NotEmpty(message = "집중 영역 목록은 필수입니다")
		List<String> focusAreas,

		@Schema(description = "팁")
		@NotEmpty(message = "팁 목록은 필수입니다")
		List<String> tips
	) {
		public Map<String, Object> toMap() {
			Map<String, Object> map = new HashMap<>();
			map.put("strengths", strengths);
			map.put("improvements", improvements);
			map.put("focus_areas", focusAreas);
			map.put("tips", tips);
			return map;
		}
	}

	@Schema(description = "오류 정보")
	public record ErrorRequest(
		@Schema(description = "턴 번호")
		Integer turn,

		@Schema(description = "오류 유형")
		String errorType,

		@Schema(description = "오류 세부 유형")
		String errorSubtype,

		@Schema(description = "오류 부분")
		String errorSegment,

		@Schema(description = "수정안")
		String correction,

		@Schema(description = "설명")
		String explanation,

		@Schema(description = "심각도")
		String severity,

		@Schema(description = "원본 텍스트")
		String originalText
	) {
		public Map<String, Object> toMap() {
			Map<String, Object> map = new HashMap<>();
			map.put("turn", turn);
			map.put("error_type", errorType);
			if (errorSubtype != null) {
				map.put("error_subtype", errorSubtype);
			}
			map.put("error_segment", errorSegment);
			map.put("correction", correction);
			map.put("explanation", explanation);
			if (severity != null) {
				map.put("severity", severity);
			}
			if (originalText != null) {
				map.put("original_text", originalText);
			}
			return map;
		}
	}

	public CreateCallAnalysisUseCaseDto toUseCaseDto() {
		List<CreateCallAnalysisUseCaseDto.ConversationDto> conversationDtos = conversation.stream()
			.map(c -> new CreateCallAnalysisUseCaseDto.ConversationDto(c.role(), c.content()))
			.toList();

		CreateCallAnalysisUseCaseDto.TaskCompletionDto taskCompletionDto = new CreateCallAnalysisUseCaseDto.TaskCompletionDto(
			taskCompletion.summary() != null
				? new CreateCallAnalysisUseCaseDto.TaskSummaryDto(
					taskCompletion.summary().overallCompletionScore(),
					taskCompletion.summary().status(),
					taskCompletion.summary().briefDescription()
				)
				: null,
			taskCompletion.slotAnalysis(),
			taskCompletion.stateAnalysis(),
			taskCompletion.completionAnalysis(),
			taskCompletion.taskSuccess()
		);

		List<CreateCallAnalysisUseCaseDto.ErrorDto> errorDtos = null;
		if (languageAccuracy.errors() != null) {
			errorDtos = languageAccuracy.errors().stream()
				.map(e -> new CreateCallAnalysisUseCaseDto.ErrorDto(
					e.turn(),
					e.errorType(),
					e.errorSubtype(),
					e.errorSegment(),
					e.correction(),
					e.explanation(),
					e.severity(),
					e.originalText()
				))
				.toList();
		}

		CreateCallAnalysisUseCaseDto.LanguageAccuracyDto languageAccuracyDto = new CreateCallAnalysisUseCaseDto.LanguageAccuracyDto(
			languageAccuracy.scoring() != null
				? new CreateCallAnalysisUseCaseDto.ScoringDto(
					languageAccuracy.scoring().finalScore(),
					languageAccuracy.scoring().rating(),
					languageAccuracy.scoring().baseScore(),
					languageAccuracy.scoring().totalDeduction(),
					languageAccuracy.scoring().levelAdjustment()
				)
				: null,
			languageAccuracy.feedback() != null
				? new CreateCallAnalysisUseCaseDto.FeedbackDto(
					languageAccuracy.feedback().strengths(),
					languageAccuracy.feedback().improvements(),
					languageAccuracy.feedback().focusAreas(),
					languageAccuracy.feedback().tips()
				)
				: null,
			languageAccuracy.briefDescription(),
			errorDtos,
			languageAccuracy.totalUserUtterances(),
			languageAccuracy.correctUtterances(),
			languageAccuracy.utteranceAnalyses(),
			languageAccuracy.errorSummary()
		);

		return new CreateCallAnalysisUseCaseDto(
			conversationDtos,
			taskCompletionDto,
			languageAccuracyDto
		);
	}
}
