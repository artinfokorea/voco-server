package com.voco.voco.app.member.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.member.domain.model.Provider;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

	boolean existsByEmail(String email);

	boolean existsByProviderAndProviderId(Provider provider, String providerId);

	Optional<MemberEntity> findByEmail(String email);

	Optional<MemberEntity> findByProviderAndProviderId(Provider provider, String providerId);
}
