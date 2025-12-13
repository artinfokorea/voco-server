package com.voco.voco.app.auth.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.auth.domain.interfaces.TokenCommandRepository;
import com.voco.voco.app.auth.domain.model.TokenEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TokenCommandRepositoryImpl implements TokenCommandRepository {

	private final TokenJpaRepository tokenJpaRepository;

	@Override
	public TokenEntity save(TokenEntity token) {
		return tokenJpaRepository.save(token);
	}
}
