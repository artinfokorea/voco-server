package com.voco.voco.app.call.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.call.domain.model.CallAnalysisEntity;

public interface CallAnalysisQueryRepository {

	Optional<CallAnalysisEntity> findById(Long id);
}
