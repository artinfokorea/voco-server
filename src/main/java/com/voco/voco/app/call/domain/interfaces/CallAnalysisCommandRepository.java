package com.voco.voco.app.call.domain.interfaces;

import com.voco.voco.app.call.domain.model.CallAnalysisEntity;

public interface CallAnalysisCommandRepository {

	Long save(CallAnalysisEntity callAnalysis);
}
