package com.voco.voco.app.call.domain.interfaces;

import java.util.Optional;

import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;

public interface CallAnalysisRawQueryRepository {

	Optional<CallAnalysisRawEntity> findByCallId(Long callId);
}
