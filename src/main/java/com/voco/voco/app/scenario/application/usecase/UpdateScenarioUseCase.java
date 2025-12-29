package com.voco.voco.app.scenario.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.scenario.application.usecase.dto.in.UpdateScenarioUseCaseDto;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.ConversationSlotEntity;
import com.voco.voco.app.scenario.domain.model.ConversationStateEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateScenarioUseCase {

	private final ScenarioQueryRepository scenarioQueryRepository;

	@Transactional
	public void execute(UpdateScenarioUseCaseDto dto) {
		ConversationScenarioEntity scenario = scenarioQueryRepository.findByIdOrThrow(dto.scenarioId());

		scenario.update(
			dto.name(),
			dto.level(),
			dto.aiRole(),
			dto.userRole(),
			dto.completionRule(),
			dto.completionRuleDetail()
		);

		ScenarioContextEntity context = ScenarioContextEntity.create(
			dto.scenarioContext().context(),
			dto.scenarioContext().personality()
		);
		scenario.updateScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			dto.languageRules().vocabularyRules(),
			dto.languageRules().sentenceRules(),
			dto.languageRules().outputConstraints()
		);
		scenario.updateLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			dto.behaviorRules().rules()
		);
		scenario.updateBehaviorRules(behaviorRules);

		if (dto.conversationStates() != null) {
			scenario.updateConversationStates(
				dto.conversationStates().stream()
					.map(s -> ConversationStateEntity.create(s.stateOrder(), s.stateName()))
					.toList()
			);
		} else {
			scenario.updateConversationStates(null);
		}

		if (dto.conversationSlots() != null) {
			scenario.updateConversationSlots(
				dto.conversationSlots().stream()
					.map(s -> ConversationSlotEntity.create(s.slotKey(), s.allowedValues()))
					.toList()
			);
		} else {
			scenario.updateConversationSlots(null);
		}
	}
}
