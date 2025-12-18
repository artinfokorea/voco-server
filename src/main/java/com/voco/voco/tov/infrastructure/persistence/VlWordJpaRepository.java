package com.voco.voco.tov.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlWordEntity;

public interface VlWordJpaRepository extends JpaRepository<VlWordEntity, UUID> {
	List<VlWordEntity> findByIdIn(List<UUID> ids);
}