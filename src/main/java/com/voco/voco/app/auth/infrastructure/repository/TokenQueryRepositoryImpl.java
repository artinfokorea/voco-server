package com.voco.voco.app.auth.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.auth.domain.interfaces.TokenQueryRepository;
import com.voco.voco.app.auth.domain.model.TokenEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenQueryRepositoryImpl implements TokenQueryRepository {

	private final TokenJpaRepository tokenJpaRepository;

	@Override
	public Optional<TokenEntity> findByMemberId(Long memberId) {
		return tokenJpaRepository.findByMemberId(memberId);
	}
}
