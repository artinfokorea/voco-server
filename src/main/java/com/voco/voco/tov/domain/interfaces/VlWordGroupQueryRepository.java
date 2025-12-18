package com.voco.voco.tov.domain.interfaces;

import java.util.Optional;
import java.util.UUID;

import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;
import com.voco.voco.tov.domain.model.VlWordGroupEntity;

public interface VlWordGroupQueryRepository {

	Optional<VlWordGroupEntity> findById(UUID id);

	default VlWordGroupEntity findByIdOrThrow(UUID id) {
		return findById(id).orElseThrow(() -> new CoreException(ApiErrorType.WORD_GROUP_NOT_FOUND));
	}
}
