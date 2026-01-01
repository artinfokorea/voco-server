package com.voco.voco.app.call.domain.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voco.voco.app.call.domain.interfaces.dto.out.CallHistoryDomainDto;
import com.voco.voco.app.call.domain.model.CallEntity;

public interface CallQueryRepository {

	CallEntity findByIdOrThrow(Long id);

	Page<CallHistoryDomainDto> findCallHistoryByMemberId(Long memberId, Pageable pageable);
}
