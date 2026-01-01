package com.voco.voco.app.call.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.call.domain.interfaces.CallAnalysisRawCommandRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisRawEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallAnalysisRawCommandRepositoryImpl implements CallAnalysisRawCommandRepository {

	private final CallAnalysisRawJpaRepository callAnalysisRawJpaRepository;

	@Override
	public Long save(CallAnalysisRawEntity callAnalysisRaw) {
		return callAnalysisRawJpaRepository.save(callAnalysisRaw).getId();
	}
}
