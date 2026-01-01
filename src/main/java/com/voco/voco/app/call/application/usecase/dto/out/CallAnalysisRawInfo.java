package com.voco.voco.app.call.application.usecase.dto.out;

import java.util.List;
import java.util.Map;

import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;

public record CallAnalysisRawInfo(
	List<ConversationRawInfo> conversation,
	Map<String, Object> taskCompletion,
	Map<String, Object> languageAccuracy
) {
	public static CallAnalysisRawInfo from(CallAnalysisRawEntity entity) {
		List<ConversationRawInfo> conversationInfo = null;
		if (entity.getConversation() != null) {
			conversationInfo = entity.getConversation().stream()
				.map(c -> new ConversationRawInfo(c.role(), c.content()))
				.toList();
		}

		return new CallAnalysisRawInfo(
			conversationInfo,
			entity.getTaskCompletion(),
			entity.getLanguageAccuracy()
		);
	}

	public record ConversationRawInfo(
		String role,
		String content
	) {
	}
}
