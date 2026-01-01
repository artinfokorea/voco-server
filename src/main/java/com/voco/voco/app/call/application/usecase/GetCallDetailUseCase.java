package com.voco.voco.app.call.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.usecase.dto.out.CallDetailInfo;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.common.enums.ApiErrorType;
import com.voco.voco.common.exception.CoreException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCallDetailUseCase {

	private final CallQueryRepository callQueryRepository;

	public CallDetailInfo execute(Long callId, Long memberId) {
		return callQueryRepository.findCallDetailByIdAndMemberId(callId, memberId)
			.map(CallDetailInfo::from)
			.orElseThrow(() -> new CoreException(ApiErrorType.CALL_NOT_FOUND));
	}
}
