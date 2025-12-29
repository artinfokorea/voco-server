package com.voco.voco.app.scenario.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScenarioQueryRepositoryImpl implements ScenarioQueryRepository {

	private final ScenarioJpaRepository scenarioJpaRepository;

	@Override
	public Optional<ConversationScenarioEntity> findById(Long id) {
		return scenarioJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public ConversationScenarioEntity findByIdOrThrow(Long id) {
		return scenarioJpaRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new CoreException(ApiErrorType.SCENARIO_NOT_FOUND));
	}

	@Override
	public Page<ConversationScenarioEntity> findAllByLevel(Level level, Pageable pageable) {
		if (level == null) {
			return scenarioJpaRepository.findByDeletedAtIsNull(pageable);
		}
		return scenarioJpaRepository.findByLevelAndDeletedAtIsNull(level, pageable);
	}
}
