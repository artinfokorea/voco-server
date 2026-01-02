package com.voco.voco.app.scenario.presentation.controller.dto.out;

import java.util.List;
import java.util.stream.Collectors;

import com.voco.voco.app.scenario.application.usecase.dto.out.ScenarioDetailInfo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "시나리오 상세 응답")
public record ScenarioDetailResponse(
	@Schema(description = "시나리오 ID", example = "1")
	Long scenarioId,

	@Schema(description = "시나리오 이름", example = "Cafe Order")
	String name,

	@Schema(description = "난이도", example = "BEGINNER")
	String level,

	@Schema(description = "AI 역할 (영어)", example = "a cafe staff member")
	String aiRoleEn,

	@Schema(description = "AI 역할 (한국어)", example = "카페 직원")
	String aiRoleKo,

	@Schema(description = "사용자 역할 (영어)", example = "a customer")
	String userRoleEn,

	@Schema(description = "사용자 역할 (한국어)", example = "손님")
	String userRoleKo,

	@Schema(description = "완료 규칙", example = "When all required information is collected")
	String completionRule,

	@Schema(description = "완료 규칙 상세")
	List<String> completionRuleDetail,

	@Schema(description = "시나리오 컨텍스트")
	ScenarioContextResponse scenarioContext,

	@Schema(description = "언어 규칙")
	LanguageRulesResponse languageRules,

	@Schema(description = "행동 규칙")
	BehaviorRulesResponse behaviorRules,

	@Schema(description = "대화 상태 목록")
	List<ConversationStateResponse> conversationStates,

	@Schema(description = "대화 슬롯 목록")
	List<ConversationSlotResponse> conversationSlots
) {
	@Schema(description = "시나리오 컨텍스트 응답")
	public record ScenarioContextResponse(
		@Schema(description = "컨텍스트 설명")
		String context,

		@Schema(description = "AI 성격 설정")
		List<String> personality
	) {
	}

	@Schema(description = "언어 규칙 응답")
	public record LanguageRulesResponse(
		@Schema(description = "어휘 규칙")
		List<String> vocabularyRules,

		@Schema(description = "문장 규칙")
		List<String> sentenceRules,

		@Schema(description = "출력 제약")
		List<String> outputConstraints
	) {
	}

	@Schema(description = "행동 규칙 응답")
	public record BehaviorRulesResponse(
		@Schema(description = "행동 규칙")
		List<String> rules
	) {
	}

	@Schema(description = "대화 상태 응답")
	public record ConversationStateResponse(
		@Schema(description = "상태 순서", example = "1")
		Integer stateOrder,

		@Schema(description = "상태 이름", example = "Greeting")
		String stateName
	) {
	}

	@Schema(description = "대화 슬롯 응답")
	public record ConversationSlotResponse(
		@Schema(description = "슬롯 키", example = "drink_type")
		String slotKey,

		@Schema(description = "허용 값 목록")
		List<String> allowedValues
	) {
	}

	public static ScenarioDetailResponse from(ScenarioDetailInfo info) {
		return new ScenarioDetailResponse(
			info.scenarioId(),
			info.name(),
			info.level(),
			info.aiRoleEn(),
			info.aiRoleKo(),
			info.userRoleEn(),
			info.userRoleKo(),
			info.completionRule(),
			info.completionRuleDetail(),
			info.scenarioContext() != null
				? new ScenarioContextResponse(
					info.scenarioContext().context(),
					info.scenarioContext().personality()
				)
				: null,
			info.languageRules() != null
				? new LanguageRulesResponse(
					info.languageRules().vocabularyRules(),
					info.languageRules().sentenceRules(),
					info.languageRules().outputConstraints()
				)
				: null,
			info.behaviorRules() != null
				? new BehaviorRulesResponse(info.behaviorRules().rules())
				: null,
			info.conversationStates() != null
				? info.conversationStates().stream()
					.map(s -> new ConversationStateResponse(s.stateOrder(), s.stateName()))
					.collect(Collectors.toList())
				: null,
			info.conversationSlots() != null
				? info.conversationSlots().stream()
					.map(s -> new ConversationSlotResponse(s.slotKey(), s.allowedValues()))
					.collect(Collectors.toList())
				: null
		);
	}
}
