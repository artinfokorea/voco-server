package com.voco.voco.app.scenario.domain.interfaces;

import java.util.List;
import java.util.Optional;

import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioEntity;

public interface ScenarioQueryRepository {

	List<ScenarioEntity> findAll();

	List<ScenarioEntity> findAllByLevel(Level level);

	Optional<ScenarioEntity> findById(Long id);
}
