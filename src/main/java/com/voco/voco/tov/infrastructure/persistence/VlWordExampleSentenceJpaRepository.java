package com.voco.voco.tov.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlWordExampleSentenceEntity;

public interface VlWordExampleSentenceJpaRepository extends JpaRepository<VlWordExampleSentenceEntity, UUID> {
	List<VlWordExampleSentenceEntity> findByMasterWordIdIn(List<UUID> masterWordIds);
}