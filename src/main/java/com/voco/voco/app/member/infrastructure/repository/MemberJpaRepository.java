package com.voco.voco.app.member.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.member.domain.model.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

	boolean existsByEmail(String email);
}