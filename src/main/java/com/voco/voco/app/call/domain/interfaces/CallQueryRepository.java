package com.voco.voco.app.call.domain.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.voco.voco.app.call.domain.interfaces.dto.CallWithScenarioDto;

public interface CallQueryRepository {

	Page<CallWithScenarioDto> findAllByMemberIdWithScenario(Long memberId, Pageable pageable);
}
