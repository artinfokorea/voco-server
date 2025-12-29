package com.voco.voco.app.scenario.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteScenarioUseCase {

	private final ScenarioQueryRepository scenarioQueryRepository;

	@Transactional
	public void execute(Long scenarioId) {
		ConversationScenarioEntity scenario = scenarioQueryRepository.findByIdOrThrow(scenarioId);
		scenario.delete();
	}
}
