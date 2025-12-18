package com.voco.voco.tov.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlExamEntity;
import com.voco.voco.tov.domain.model.enums.VlExamStatus;

public interface VlExamJpaRepository extends JpaRepository<VlExamEntity, UUID> {
	boolean existsByUserIdAndStatus(UUID userId, VlExamStatus status);
}