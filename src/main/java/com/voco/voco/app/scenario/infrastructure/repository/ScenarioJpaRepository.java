package com.voco.voco.app.scenario.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.scenario.domain.model.ConversationScenarioEntity;
import com.voco.voco.app.scenario.domain.model.Level;

public interface ScenarioJpaRepository extends JpaRepository<ConversationScenarioEntity, Long> {
	Optional<ConversationScenarioEntity> findByIdAndDeletedAtIsNull(Long id);

	Page<ConversationScenarioEntity> findByDeletedAtIsNull(Pageable pageable);

	Page<ConversationScenarioEntity> findByLevelAndDeletedAtIsNull(Level level, Pageable pageable);
}
