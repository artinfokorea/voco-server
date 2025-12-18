package com.voco.voco.tov.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlWordSynonymEntity;

public interface VlWordSynonymJpaRepository extends JpaRepository<VlWordSynonymEntity, UUID> {
	List<VlWordSynonymEntity> findByMasterWordIdIn(List<UUID> masterWordIds);
}