package com.voco.voco.app.call.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.usecase.dto.out.CallAnalysisInfo;
import com.voco.voco.app.call.domain.interfaces.CallAnalysisQueryRepository;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCallAnalysisUseCase {

	private final CallAnalysisQueryRepository callAnalysisQueryRepository;
	private final CallQueryRepository callQueryRepository;

	public CallAnalysisInfo execute(Long analysisId, Long memberId) {
		if (!callQueryRepository.existsByAnalysisIdAndMemberId(analysisId, memberId)) {
			throw new CoreException(ApiErrorType.CALL_ANALYSIS_FORBIDDEN);
		}

		return callAnalysisQueryRepository.findById(analysisId)
			.map(CallAnalysisInfo::from)
			.orElseThrow(() -> new CoreException(ApiErrorType.CALL_ANALYSIS_NOT_FOUND));
	}
}
