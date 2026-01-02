package com.voco.voco.app.scenario.presentation.controller.dto.in;

import java.util.List;

import com.voco.voco.app.scenario.application.usecase.dto.in.UpdateScenarioUseCaseDto;
import com.voco.voco.app.scenario.domain.model.Level;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "시나리오 수정 요청")
public record UpdateScenarioRequest(
	@Schema(description = "시나리오 이름", example = "Cafe Order")
	@NotBlank @Size(max = 100) String name,

	@Schema(description = "난이도", example = "BEGINNER")
	@NotNull Level level,

	@Schema(description = "AI 역할 (영어)", example = "a cafe staff member")
	@NotBlank @Size(max = 50) String aiRoleEn,

	@Schema(description = "AI 역할 (한국어)", example = "카페 직원")
	@NotBlank @Size(max = 50) String aiRoleKo,

	@Schema(description = "사용자 역할 (영어)", example = "a customer")
	@NotBlank @Size(max = 50) String userRoleEn,

	@Schema(description = "사용자 역할 (한국어)", example = "손님")
	@NotBlank @Size(max = 50) String userRoleKo,

	@Schema(description = "완료 규칙", example = "When all required information (drink_type, size, temperature) is collected")
	@NotBlank @Size(max = 100) String completionRule,

	@Schema(description = "완료 규칙 상세", example = "[\"Confirm the order\", \"Politely end the conversation\"]")
	List<String> completionRuleDetail,

	@Schema(description = "시나리오 컨텍스트")
	@NotNull ScenarioContextRequest scenarioContext,

	@Schema(description = "언어 규칙")
	@NotNull LanguageRulesRequest languageRules,

	@Schema(description = "행동 규칙")
	@NotNull BehaviorRulesRequest behaviorRules,

	@Schema(description = "대화 상태 목록")
	List<ConversationStateRequest> conversationStates,

	@Schema(description = "대화 슬롯 목록")
	List<ConversationSlotRequest> conversationSlots
) {
	@Schema(description = "시나리오 컨텍스트 요청")
	public record ScenarioContextRequest(
		@Schema(description = "컨텍스트 설명", example = "The user is visiting a cafe for the first time and wants to order a drink.")
		@NotBlank String context,

		@Schema(description = "AI 성격 설정", example = "[\"Friendly and polite\", \"Calm and patient\", \"Not too talkative\"]")
		@NotNull List<String> personality
	) {
	}

	@Schema(description = "언어 규칙 요청")
	public record LanguageRulesRequest(
		@Schema(description = "어휘 규칙", example = "[\"Use simple vocabulary only\", \"Avoid slang\"]")
		@NotNull List<String> vocabularyRules,

		@Schema(description = "문장 규칙", example = "[\"Use short sentences (max 8 words per sentence)\", \"Ask ONLY one question at a time\"]")
		@NotNull List<String> sentenceRules,

		@Schema(description = "출력 제약", example = "[\"Use simple English only\", \"Avoid long sentences\"]")
		@NotNull List<String> outputConstraints
	) {
	}

	@Schema(description = "행동 규칙 요청")
	public record BehaviorRulesRequest(
		@Schema(description = "행동 규칙", example = "[\"NEVER explain grammar or vocabulary.\", \"NEVER break character.\"]")
		@NotNull List<String> rules
	) {
	}

	@Schema(description = "대화 상태 요청")
	public record ConversationStateRequest(
		@Schema(description = "상태 순서", example = "1")
		@NotNull Integer stateOrder,

		@Schema(description = "상태 이름", example = "Greeting")
		@NotBlank @Size(max = 50) String stateName
	) {
	}

	@Schema(description = "대화 슬롯 요청")
	public record ConversationSlotRequest(
		@Schema(description = "슬롯 키", example = "drink_type")
		@NotBlank @Size(max = 50) String slotKey,

		@Schema(description = "허용 값 목록", example = "[\"coffee\", \"latte\", \"americano\"]")
		@NotNull List<String> allowedValues
	) {
	}

	public UpdateScenarioUseCaseDto toUseCaseDto(Long scenarioId) {
		return new UpdateScenarioUseCaseDto(
			scenarioId,
			name,
			level,
			aiRoleEn,
			aiRoleKo,
			userRoleEn,
			userRoleKo,
			completionRule,
			completionRuleDetail,
			new UpdateScenarioUseCaseDto.ScenarioContextDto(
				scenarioContext.context(),
				scenarioContext.personality()
			),
			new UpdateScenarioUseCaseDto.LanguageRulesDto(
				languageRules.vocabularyRules(),
				languageRules.sentenceRules(),
				languageRules.outputConstraints()
			),
			new UpdateScenarioUseCaseDto.BehaviorRulesDto(
				behaviorRules.rules()
			),
			conversationStates != null
				? conversationStates.stream()
				.map(s -> new UpdateScenarioUseCaseDto.ConversationStateDto(s.stateOrder(), s.stateName()))
				.toList()
				: null,
			conversationSlots != null
				? conversationSlots.stream()
				.map(s -> new UpdateScenarioUseCaseDto.ConversationSlotDto(s.slotKey(), s.allowedValues()))
				.toList()
				: null
		);
	}
}
