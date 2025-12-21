package com.voco.voco.app.member.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.member.domain.model.MemberEntity;
import com.voco.voco.app.member.domain.model.Provider;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

public interface MemberQueryRepository {

	boolean existsByEmail(String email);

	boolean existsByProviderAndProviderId(Provider provider, String providerId);

	Optional<MemberEntity> findByEmail(String email);

	Optional<MemberEntity> findByProviderAndProviderId(Provider provider, String providerId);

	Optional<MemberEntity> findById(Long id);

	default MemberEntity findByIdOrThrow(Long id) {
		return findById(id).orElseThrow(() -> new CoreException(ApiErrorType.MEMBER_NOT_FOUND));
	}
}
