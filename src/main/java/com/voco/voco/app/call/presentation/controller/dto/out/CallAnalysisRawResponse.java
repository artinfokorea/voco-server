package com.voco.voco.app.call.presentation.controller.dto.out;

import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisRawInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "통화 분석 Raw 데이터 응답")
public record CallAnalysisRawResponse(
	@Schema(description = "대화 내역")
	List<ConversationRawResponse> conversation,

	@Schema(description = "과제 완료 분석")
	Map<String, Object> taskCompletion,

	@Schema(description = "언어 정확도 분석")
	Map<String, Object> languageAccuracy
) {
	public static CallAnalysisRawResponse from(CallAnalysisRawInfo info) {
		List<ConversationRawResponse> conversationResponse = null;
		if (info.conversation() != null) {
			conversationResponse = info.conversation().stream()
				.map(c -> new ConversationRawResponse(c.role(), c.content()))
				.toList();
		}

		return new CallAnalysisRawResponse(
			conversationResponse,
			info.taskCompletion(),
			info.languageAccuracy()
		);
	}

	@Schema(description = "대화 항목")
	public record ConversationRawResponse(
		@Schema(description = "역할 (user/assistant)")
		String role,

		@Schema(description = "대화 내용")
		String content
	) {
	}
}
