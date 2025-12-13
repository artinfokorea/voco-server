package com.voco.voco.app.scenario.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.scenario.domain.interfaces.ScenarioQueryRepository;
import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ScenarioQueryRepositoryImpl implements ScenarioQueryRepository {

	private final ScenarioJpaRepository scenarioJpaRepository;

	@Override
	public List<ScenarioEntity> findAll() {
		return scenarioJpaRepository.findAll();
	}

	@Override
	public List<ScenarioEntity> findAllByLevel(Level level) {
		return scenarioJpaRepository.findAllByLevel(level);
	}

	@Override
	public Optional<ScenarioEntity> findById(Long id) {
		return scenarioJpaRepository.findById(id);
	}
}
