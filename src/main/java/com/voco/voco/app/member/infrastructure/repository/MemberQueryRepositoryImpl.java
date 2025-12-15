package com.voco.voco.app.member.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.member.domain.interfaces.MemberQueryRepository;
import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.member.domain.model.Provider;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

	private final MemberJpaRepository memberJpaRepository;

	@Override
	public boolean existsByEmail(String email) {
		return memberJpaRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByProviderAndProviderId(Provider provider, String providerId) {
		return memberJpaRepository.existsByProviderAndProviderId(provider, providerId);
	}

	@Override
	public Optional<MemberEntity> findByEmail(String email) {
		return memberJpaRepository.findByEmail(email);
	}

	@Override
	public Optional<MemberEntity> findByProviderAndProviderId(Provider provider, String providerId) {
		return memberJpaRepository.findByProviderAndProviderId(provider, providerId);
	}

	@Override
	public Optional<MemberEntity> findById(Long id) {
		return memberJpaRepository.findById(id);
	}
}
