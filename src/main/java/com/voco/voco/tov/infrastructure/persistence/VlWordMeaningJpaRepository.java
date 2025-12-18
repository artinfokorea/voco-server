package com.voco.voco.tov.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlWordMeaningEntity;

public interface VlWordMeaningJpaRepository extends JpaRepository<VlWordMeaningEntity, UUID> {
	List<VlWordMeaningEntity> findByMasterWordIdIn(List<UUID> masterWordIds);
}