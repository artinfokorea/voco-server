package com.voco.voco.app.scenario.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.scenario.domain.model.Level;
import com.voco.voco.app.scenario.domain.model.ScenarioEntity;

public interface ScenarioJpaRepository extends JpaRepository<ScenarioEntity, Long> {

	List<ScenarioEntity> findAllByLevel(Level level);
}
