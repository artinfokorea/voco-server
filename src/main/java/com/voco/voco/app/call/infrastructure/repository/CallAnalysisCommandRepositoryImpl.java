package com.voco.voco.app.call.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.call.domain.interfaces.CallAnalysisCommandRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallAnalysisCommandRepositoryImpl implements CallAnalysisCommandRepository {

	private final CallAnalysisJpaRepository callAnalysisJpaRepository;

	@Override
	public Long save(CallAnalysisEntity callAnalysis) {
		return callAnalysisJpaRepository.save(callAnalysis).getId();
	}
}
