package com.voco.voco.app.call.presentation.controller.dto.out;

import java.util.List;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisInfo;
import com.voco.voco.app.call.domain.enums.CallAnalysisGrade;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 분석 결과 응답")
public record CallAnalysisResponse(
	@Schema(description = "분석 결과 ID")
	Long id,

	@Schema(description = "등급")
	CallAnalysisGrade grade,

	@Schema(description = "종합 점수")
	Integer overallScore,

	@Schema(description = "과제 완료 점수")
	Integer taskCompletionScore,

	@Schema(description = "과제 완료 요약")
	String taskCompletionSummary,

	@Schema(description = "언어 정확도 점수")
	Integer languageAccuracyScore,

	@Schema(description = "언어 정확도 요약")
	String languageAccuracySummary,

	@Schema(description = "대화 내역")
	List<ConversationResponse> conversation,

	@Schema(description = "피드백")
	FeedbackResponse feedback
) {
	@Schema(description = "대화 항목")
	public record ConversationResponse(
		@Schema(description = "역할") String role,
		@Schema(description = "내용") String content,
		@Schema(description = "오류 정보") ConversationErrorResponse error
	) {
	}

	@Schema(description = "대화 오류 정보")
	public record ConversationErrorResponse(
		@Schema(description = "오류 유형") String errorType,
		@Schema(description = "오류 세부 유형") String errorSubtype,
		@Schema(description = "오류 부분") String errorSegment,
		@Schema(description = "수정안") String correction,
		@Schema(description = "설명") String explanation
	) {
	}

	@Schema(description = "피드백 정보")
	public record FeedbackResponse(
		@Schema(description = "강점") List<String> strengths,
		@Schema(description = "개선점") List<String> improvements,
		@Schema(description = "집중 영역") List<String> focusAreas,
		@Schema(description = "팁") List<String> tips
	) {
	}

	public static CallAnalysisResponse from(CallAnalysisInfo info) {
		List<ConversationResponse> conversationResponses = null;
		if (info.conversation() != null) {
			conversationResponses = info.conversation().stream()
				.map(c -> new ConversationResponse(
					c.role(),
					c.content(),
					c.error() != null
						? new ConversationErrorResponse(
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

		FeedbackResponse feedbackResponse = null;
		if (info.feedback() != null) {
			feedbackResponse = new FeedbackResponse(
				info.feedback().strengths(),
				info.feedback().improvements(),
				info.feedback().focusAreas(),
				info.feedback().tips()
			);
		}

		return new CallAnalysisResponse(
			info.id(),
			info.grade(),
			info.overallScore(),
			info.taskCompletionScore(),
			info.taskCompletionSummary(),
			info.languageAccuracyScore(),
			info.languageAccuracySummary(),
			conversationResponses,
			feedbackResponse
		);
	}
}
