package com.voco.voco.tov.domain.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.tov.domain.model.VlUserEntity;

public interface VlUserQueryRepository {

	Optional<VlUserEntity> findById(UUID id);

	default VlUserEntity findByIdOrThrow(UUID id) {
		return findById(id).orElseThrow(() -> new CoreException(ApiErrorType.MEMBER_NOT_FOUND));
	}
}