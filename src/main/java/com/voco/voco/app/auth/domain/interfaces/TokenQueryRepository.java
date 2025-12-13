package com.voco.voco.app.auth.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.auth.domain.model.TokenEntity;

public interface TokenQueryRepository {

	Optional<TokenEntity> findByMemberId(Long memberId);
}
