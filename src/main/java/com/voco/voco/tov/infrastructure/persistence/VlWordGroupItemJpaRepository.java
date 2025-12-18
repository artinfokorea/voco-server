package com.voco.voco.tov.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlWordGroupItemEntity;

public interface VlWordGroupItemJpaRepository extends JpaRepository<VlWordGroupItemEntity, UUID> {

	List<VlWordGroupItemEntity> findByWordGroupIdAndWordSeqBetween(UUID wordGroupId, Integer from, Integer to);
}