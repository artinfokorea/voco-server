package com.voco.voco.tov.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.tov.domain.model.VlWordGroupEntity;

public interface VlWordGroupJpaRepository extends JpaRepository<VlWordGroupEntity, UUID> {
}