package com.voco.voco.app.scenario.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.scenario.application.usecase.dto.in.UpdateScenarioUseCaseDto;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.ConversationSlotEntity;
import com.voco.voco.app.scenario.domain.model.ConversationStateEntity;

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

		scenario.updateScenarioContext(
			dto.scenarioContext().context(),
			dto.scenarioContext().personality()
		);

		scenario.updateLanguageRules(
			dto.languageRules().vocabularyRules(),
			dto.languageRules().sentenceRules(),
			dto.languageRules().outputConstraints()
		);

		scenario.updateBehaviorRules(dto.behaviorRules().rules());

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
