package com.voco.voco.app.scenario.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;

public interface ScenarioJpaRepository extends JpaRepository<ConversationScenarioEntity, Long> {
}
