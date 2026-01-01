package com.voco.voco.app.call.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.voco.voco.app.call.application.usecase.dto.out.AdminCallHistoryInfo;
import com.voco.voco.app.call.domain.interfaces.CallQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAdminCallsUseCase {

	private final CallQueryRepository callQueryRepository;

	public Page<AdminCallHistoryInfo> execute(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return callQueryRepository.findAllCallHistory(pageable)
			.map(AdminCallHistoryInfo::from);
	}
}
