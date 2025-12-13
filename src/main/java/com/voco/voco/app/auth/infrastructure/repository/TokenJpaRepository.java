package com.voco.voco.app.auth.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.auth.domain.model.TokenEntity;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, Long> {

	Optional<TokenEntity> findByMemberId(Long memberId);
}