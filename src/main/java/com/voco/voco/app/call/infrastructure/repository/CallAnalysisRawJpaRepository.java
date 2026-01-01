package com.voco.voco.app.call.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;

public interface CallAnalysisRawJpaRepository extends JpaRepository<CallAnalysisRawEntity, Long> {
}
