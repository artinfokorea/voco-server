package com.voco.voco.tov.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlWordAntonymEntity;

public interface VlWordAntonymJpaRepository extends JpaRepository<VlWordAntonymEntity, UUID> {
	List<VlWordAntonymEntity> findByMasterWordIdIn(List<UUID> masterWordIds);
}