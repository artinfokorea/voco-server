package com.voco.voco.app.scenario.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

public interface ScenarioQueryRepository {
	Optional<ConversationScenarioEntity> findById(Long id);

	ConversationScenarioEntity findByIdOrThrow(Long id);
}
