package com.voco.voco.app.scenario.domain.interfaces;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.Level;

public interface ScenarioQueryRepository {
	Optional<ConversationScenarioEntity> findById(Long id);

	ConversationScenarioEntity findByIdOrThrow(Long id);

	Page<ConversationScenarioEntity> findAllByLevel(Level level, Pageable pageable);
}
