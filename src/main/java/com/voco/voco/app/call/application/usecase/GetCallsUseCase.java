package com.voco.voco.app.call.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.usecase.dto.out.CallHistoryInfo;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;
import com.voco.voco.app.call.domain.interfaces.dto.out.CallHistoryDomainDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetCallsUseCase {

	private final CallQueryRepository callQueryRepository;

	@Transactional(readOnly = true)
	public Page<CallHistoryInfo> execute(Long memberId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<CallHistoryDomainDto> domainPage = callQueryRepository.findCallHistoryByMemberId(memberId, pageable);
		return domainPage.map(CallHistoryInfo::from);
	}
}
