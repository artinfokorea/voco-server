package com.voco.voco.app.call.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisRawInfo;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisRawQueryRepository;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAdminCallAnalysisRawUseCase {

	private final CallAnalysisRawQueryRepository callAnalysisRawQueryRepository;

	public CallAnalysisRawInfo execute(Long callId) {
		return callAnalysisRawQueryRepository.findByCallId(callId)
			.map(CallAnalysisRawInfo::from)
			.orElseThrow(() -> new CoreException(ApiErrorType.CALL_ANALYSIS_NOT_FOUND));
	}
}
