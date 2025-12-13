package com.voco.voco.app.call.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.voco.voco.app.call.domain.interfaces.CallAnalysisQueryRepository;
import com.voco.voco.app.call.domain.model.CallAnalysisEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CallAnalysisQueryRepositoryImpl implements CallAnalysisQueryRepository {

	private final CallAnalysisJpaRepository callAnalysisJpaRepository;

	@Override
	public Optional<CallAnalysisEntity> findById(Long id) {
		return callAnalysisJpaRepository.findById(id);
	}
}
