package com.voco.voco.app.scenario.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScenarioQueryRepositoryImpl implements ScenarioQueryRepository {

	private final ScenarioJpaRepository scenarioJpaRepository;

	@Override
	public Optional<ConversationScenarioEntity> findById(Long id) {
		return scenarioJpaRepository.findById(id);
	}

	@Override
	public ConversationScenarioEntity findByIdOrThrow(Long id) {
		return scenarioJpaRepository.findById(id)
			.orElseThrow(() -> new CoreException(ApiErrorType.SCENARIO_NOT_FOUND));
	}
}
