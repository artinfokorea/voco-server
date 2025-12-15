package com.voco.voco.app.member.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.member.domain.model.Provider;

public interface MemberQueryRepository {

	boolean existsByEmail(String email);

	boolean existsByProviderAndProviderId(Provider provider, String providerId);

	Optional<MemberEntity> findByEmail(String email);

	Optional<MemberEntity> findByProviderAndProviderId(Provider provider, String providerId);

	Optional<MemberEntity> findById(Long id);
}
