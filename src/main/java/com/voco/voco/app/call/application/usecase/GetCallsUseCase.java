package com.voco.voco.app.call.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.usecase.dto.out.CallInfo;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCallsUseCase {

	private final CallQueryRepository callQueryRepository;

	public Page<CallInfo> execute(Long memberId, Pageable pageable) {
		return callQueryRepository.findAllByMemberIdWithScenario(memberId, pageable)
			.map(CallInfo::from);
	}
}
