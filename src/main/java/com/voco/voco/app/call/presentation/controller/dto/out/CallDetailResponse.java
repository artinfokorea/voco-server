package com.voco.voco.app.call.presentation.controller.dto.out;

import java.time.LocalDateTime;
import java.util.List;

import com.voco.voco.app.call.application.usecase.dto.out.CallDetailInfo;
import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;
import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 상세 응답")
public record CallDetailResponse(
	@Schema(description = "통화 일시")
	LocalDateTime createdAt,

	@Schema(description = "시나리오 제목")
	String scenarioName,

	@Schema(description = "시나리오 레벨")
	Level scenarioLevel,

	@Schema(description = "분석 등급")
	CallAnalysisGrade grade,

	@Schema(description = "분석 점수")
	Integer overallScore,

	@Schema(description = "대화 내용")
	List<ConversationEntryResponse> conversation,

	@Schema(description = "과제 완료 점수")
	Integer taskCompletionScore,

	@Schema(description = "과제 완료 요약")
	String taskCompletionSummary,

	@Schema(description = "언어 정확도 점수")
	Integer languageAccuracyScore,

	@Schema(description = "언어 정확도 요약")
	String languageAccuracySummary,

	@Schema(description = "피드백")
	FeedbackResponse feedback
) {
	public static CallDetailResponse from(CallDetailInfo info) {
		List<ConversationEntryResponse> conversationResponse = null;
		if (info.conversation() != null) {
			conversationResponse = info.conversation().stream()
				.map(ConversationEntryResponse::from)
				.toList();
		}

		FeedbackResponse feedbackResponse = null;
		if (info.feedback() != null) {
			feedbackResponse = FeedbackResponse.from(info.feedback());
		}

		return new CallDetailResponse(
			info.createdAt(),
			info.scenarioName(),
			info.scenarioLevel(),
			info.grade(),
			info.overallScore(),
			conversationResponse,
			info.taskCompletionScore(),
			info.taskCompletionSummary(),
			info.languageAccuracyScore(),
			info.languageAccuracySummary(),
			feedbackResponse
		);
	}

	@Schema(description = "대화 항목")
	public record ConversationEntryResponse(
		@Schema(description = "역할 (user/assistant)")
		String role,

		@Schema(description = "내용")
		String content,

		@Schema(description = "오류 정보")
		ConversationErrorResponse error
	) {
		public static ConversationEntryResponse from(CallDetailInfo.ConversationEntryInfo entry) {
			ConversationErrorResponse errorResponse = null;
			if (entry.error() != null) {
				errorResponse = ConversationErrorResponse.from(entry.error());
			}
			return new ConversationEntryResponse(entry.role(), entry.content(), errorResponse);
		}
	}

	@Schema(description = "대화 오류 정보")
	public record ConversationErrorResponse(
		@Schema(description = "오류 유형")
		String errorType,

		@Schema(description = "오류 하위 유형")
		String errorSubtype,

		@Schema(description = "오류 부분")
		String errorSegment,

		@Schema(description = "수정")
		String correction,

		@Schema(description = "설명")
		String explanation
	) {
		public static ConversationErrorResponse from(CallDetailInfo.ConversationErrorInfo error) {
			return new ConversationErrorResponse(
				error.errorType(),
				error.errorSubtype(),
				error.errorSegment(),
				error.correction(),
				error.explanation()
			);
		}
	}

	@Schema(description = "피드백")
	public record FeedbackResponse(
		@Schema(description = "강점")
		List<String> strengths,

		@Schema(description = "개선점")
		List<String> improvements,

		@Schema(description = "집중 영역")
		List<String> focusAreas,

		@Schema(description = "팁")
		List<String> tips
	) {
		public static FeedbackResponse from(CallDetailInfo.FeedbackInfo feedback) {
			return new FeedbackResponse(
				feedback.strengths(),
				feedback.improvements(),
				feedback.focusAreas(),
				feedback.tips()
			);
		}
	}
}
