package com.voco.voco.app.call.domain.interfaces;

import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;

public interface CallAnalysisRawCommandRepository {

	Long save(CallAnalysisRawEntity callAnalysisRaw);
}
