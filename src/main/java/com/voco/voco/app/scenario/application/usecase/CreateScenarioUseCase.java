package com.voco.voco.app.scenario.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.scenario.application.usecase.dto.in.CreateScenarioUseCaseDto;
import com.voco.voco.app.scenario.domain.interfaces.ScenarioCommandRepository;
import com.voco.voco.app.scenario.domain.model.BehaviorRulesEntity;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.ConversationSlotEntity;
import com.voco.voco.app.scenario.domain.model.ConversationStateEntity;
import com.voco.voco.app.scenario.domain.model.LanguageRulesEntity;
import com.voco.voco.app.scenario.domain.model.ScenarioContextEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateScenarioUseCase {

	private final ScenarioCommandRepository scenarioCommandRepository;

	@Transactional
	public Long execute(CreateScenarioUseCaseDto dto) {
		ConversationScenarioEntity scenario = ConversationScenarioEntity.create(
			dto.name(),
			dto.level(),
			dto.aiRoleEn(),
			dto.aiRoleKo(),
			dto.userRoleEn(),
			dto.userRoleKo(),
			dto.completionRule(),
			dto.completionRuleDetail()
		);

		ScenarioContextEntity context = ScenarioContextEntity.create(
			dto.scenarioContext().context(),
			dto.scenarioContext().personality()
		);
		scenario.addScenarioContext(context);

		LanguageRulesEntity languageRules = LanguageRulesEntity.create(
			dto.languageRules().vocabularyRules(),
			dto.languageRules().sentenceRules(),
			dto.languageRules().outputConstraints()
		);
		scenario.addLanguageRules(languageRules);

		BehaviorRulesEntity behaviorRules = BehaviorRulesEntity.create(
			dto.behaviorRules().rules()
		);
		scenario.addBehaviorRules(behaviorRules);

		if (dto.conversationStates() != null) {
			dto.conversationStates().forEach(s -> {
				ConversationStateEntity state = ConversationStateEntity.create(s.stateOrder(), s.stateName());
				scenario.addConversationState(state);
			});
		}

		if (dto.conversationSlots() != null) {
			dto.conversationSlots().forEach(s -> {
				ConversationSlotEntity slot = ConversationSlotEntity.create(s.slotKey(), s.allowedValues());
				scenario.addConversationSlot(slot);
			});
		}

		return scenarioCommandRepository.save(scenario);
	}
}
