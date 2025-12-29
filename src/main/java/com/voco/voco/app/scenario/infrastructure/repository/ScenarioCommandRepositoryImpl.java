package com.voco.voco.app.scenario.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.scenario.domain.interfaces.ScenarioCommandRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScenarioCommandRepositoryImpl implements ScenarioCommandRepository {

	private final ScenarioJpaRepository scenarioJpaRepository;

	@Override
	public Long save(ConversationScenarioEntity scenario) {
		return scenarioJpaRepository.save(scenario).getId();
	}
}
