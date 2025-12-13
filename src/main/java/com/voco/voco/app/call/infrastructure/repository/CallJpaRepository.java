package com.voco.voco.app.call.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.call.domain.model.CallEntity;

public interface CallJpaRepository extends JpaRepository<CallEntity, Long> {
}
