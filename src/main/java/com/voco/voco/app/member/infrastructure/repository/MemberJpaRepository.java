package com.voco.voco.app.member.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.member.domain.model.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

	boolean existsByEmail(String email);

	Optional<MemberEntity> findByEmail(String email);
}